package com.jointsky.edps.spider.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.jointsky.edps.spider.common.SelectType;
import com.jointsky.edps.spider.common.SysConstant;
import com.jointsky.edps.spider.config.*;
import com.jointsky.edps.spider.filter.ValueFilter;
import com.jointsky.edps.spider.utils.ProcessorUtils;
import com.jointsky.edps.spider.utils.SpiderUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.AbstractSelectable;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class JsonProcessor implements PageProcessor {

    private Site site;
    private SiteConfig siteConfig;

    public JsonProcessor(SiteConfig siteConfig) {
        this.siteConfig = siteConfig;
        this.site = SpiderUtils.buildSite(this.siteConfig);
    }

    @Override
    public void process(Page page) {
        Request request = page.getRequest();
        Object extra = request.getExtra(SysConstant.URL_ID);
        if (extra == null) {
            StaticLog.error("未知链接：" + request.getUrl());
            page.setSkip(true);
            return;
        }
        String urlId = extra.toString();
        PageConfig pageConfig = this.siteConfig.getAllPageConfig().get(urlId);
        dealHelpSelect(page, pageConfig);
        dealTargetSelect(page, pageConfig);
        dealTargetUrl(page, pageConfig);
    }

    private void dealTargetUrl(Page page, PageConfig pageConfig) {
        if (!pageConfig.isTargetUrl()){
            return;
        }
        List<FieldSelectConfig> staticFields = pageConfig.getStaticFields();
        List<ResultSelectConfig> fieldSelect = pageConfig.getFieldSelect();
        if (fieldSelect == null || fieldSelect.size() <=0){
            return;
        }
        AbstractSelectable selectable = pageConfig.isJsonType() ? page.getJson() : page.getHtml();
        Map<String, List<String>> resultMap = new HashMap<>();
        String primaryKey = "";
        for (FieldSelectConfig fConfig : fieldSelect) {
            Selectable selectVal = ProcessorUtils.getSelectVal(staticFields, selectable, fConfig);
            if (selectVal == null) {
                continue;
            }
            List<String> valList = selectVal.all();
            String confText = fConfig.getConfigText();
            if (fConfig.getSelectType() == SelectType.JPATH && confText.contains("*")) {
                if (StrUtil.isBlank(primaryKey)) {
                    primaryKey = confText;
                }
                if (confText.endsWith("*")){
                    primaryKey = confText;
                }
            }
            String filedName = fConfig.getFiledName();
            if (resultMap.containsKey(filedName)){
                resultMap.get(filedName).addAll(valList);
            }
            else{
                resultMap.put(filedName, valList);
            }
        }
        if (!pageConfig.isJsonType() || !primaryKey.contains("*")){
            Map<String, Object> singleMap = new HashMap<>();
            resultMap.forEach((k,v)->{
                if (v.size() == 1){
                    singleMap.put(k, v.get(0));
                }
                else{
                    singleMap.put(k, v);
                }
            });
            filterCheck(singleMap, page, pageConfig);
            return;
        }
        List<String> strList = resultMap.get(primaryKey);
        int size = strList.size();
        Map<String, Object> singleMap = new HashMap<>();
        if (size > 1){
            resultMap.forEach((k,v)->{
                if (v.size() == 1){
                    singleMap.put(k, v.get(0));
                }
                else if (v.size() < size){
                    singleMap.put(k, v);
                }
            });
        }
        for (int i = 0; i < size; i++) {
            if (StrUtil.isBlank(strList.get(i))){
                continue;
            }
            if (primaryKey.endsWith("*") && !JSONUtil.isJson(strList.get(i))){
                continue;
            }
            Map<String, Object> singleItem = new HashMap<>();
            singleMap.forEach(singleMap:: put);
            boolean mapUpdate = false;
            for (Map.Entry<String, List<String>> entry : resultMap.entrySet()) {
                String k = entry.getKey();
                List<String> v = entry.getValue();
                if (v.size() == size) {
                    if (k.endsWith("*")) {
                        String s = v.get(i);
                        JSONObject jsonObject = JSONUtil.parseObj(s);
                        jsonObject.forEach(singleItem::put);
                    }
                    else{
                        singleItem.put(k, v.get(i));
                    }
                    mapUpdate = true;
                }
            }
            if (!mapUpdate){
                filterCheck(singleMap, page, pageConfig);
                return;
            }
            filterCheck(singleMap, page, pageConfig);
        }
    }

    private void filterCheck(Map<String, Object> resultMap, Page page, PageConfig pageConfig) {
        List<ResultSelectConfig> fieldSelect = pageConfig.getFieldSelect();
        for (ResultSelectConfig config : fieldSelect) {
            List<AbstractMap.SimpleEntry<String, Map<String, Object>>> filters = config.getFilters();
            Object val = resultMap.getOrDefault(config.getFiledName(), null);
            for (AbstractMap.SimpleEntry<String, Map<String, Object>> str : filters) {
                ValueFilter filter = SpiderUtils.buildValueFilter(str.getKey());
                if (filter == null) {
                    continue;
                }
                filter.setValue(val);
                filter.setSettings(str.getValue());
                if (filter.shouldRemove()){
                    return;
                }
                val = filter.parseValue();
            }
            resultMap.put(config.getFiledName(), val);
        }
        page.putField(SysConstant.SINGLE_ITEM, resultMap);
    }

    private void dealTargetSelect(Page page, PageConfig pageConfig) {
        List<UrlSelectConfig> targetSelect = pageConfig.getTargetSelect();
        if (targetSelect == null || targetSelect.size() <= 0) {
            return;
        }
        List<FieldSelectConfig> newStaticFields = pageConfig.getDealStaticFields();
        AbstractSelectable selectable = pageConfig.isJsonType() ? page.getJson() : page.getHtml();
        PageConfig nextPage = pageConfig.getNextTargetConfig();
        String configId = (pageConfig.isAllStaticFiles() ? "" : SecureUtil.md5(page.getRequest().getUrl())) + nextPage.getId();
        nextPage.getStaticFields().addAll(newStaticFields);
        this.siteConfig.getAllPageConfig().put(configId, nextPage);
        if (pageConfig.isJsonType()) {
            @SuppressWarnings("ConstantConditions")
            Json json = (Json) selectable;
            for (UrlSelectConfig hConfig : targetSelect) {
                SelectType selectType = hConfig.getSelectType();
                List<String> helpLinks = new ArrayList<>();
                switch (selectType) {
                    case JPATH:
                        helpLinks = fixHelpUrls(json.jsonPath(hConfig.getConfigText()).all(), page.getRequest().getUrl());
                        break;
                    case CUSTOM:
                        helpLinks = dealCustomTargetLink(newStaticFields, json, hConfig);
                        break;
                    default:
                        break;
                }
                addLinksToPage(helpLinks, configId, page);
            }
            return;
        }
        @SuppressWarnings("ConstantConditions")
        Html pageHtml = (Html) selectable;
        Selectable htmlLinks = pageHtml.links();
        for (UrlSelectConfig hConfig : targetSelect) {
            SelectType selectType = hConfig.getSelectType();
            List<String> helpLinks = new ArrayList<>();
            switch (selectType) {
                case CSS:
                    helpLinks = htmlLinks.$(hConfig.getConfigText()).all();
                    break;
                case REGEX:
                    helpLinks = htmlLinks.regex(hConfig.getConfigText()).all();
                    break;
                case XPATH:
                    helpLinks = htmlLinks.xpath(hConfig.getConfigText()).all();
                    break;
                case JPATH:
                    helpLinks = htmlLinks.jsonPath(hConfig.getConfigText()).all();
                    break;
                case CUSTOM:
                    helpLinks = dealCustomTargetLink(newStaticFields, pageHtml, hConfig);
                    break;
                default:
                    break;
            }
            addLinksToPage(helpLinks, configId, page);
        }


    }

    private static final Pattern pattern = Pattern.compile("\\{([^}]+)}");
    private List<String> dealCustomTargetLink(List<FieldSelectConfig> newStaticFields, AbstractSelectable selectable, UrlSelectConfig hConfig) {
        List<String> helpLinks = new ArrayList<>();
        String customText = hConfig.getConfigText();
        if (StrUtil.isBlank(customText)){
            return helpLinks;
        }
        for (FieldSelectConfig fieldConfig : newStaticFields) {
            customText = customText.replaceAll("{\\s*" + fieldConfig.getFiledName() + "\\s*}", fieldConfig.getConfigText());
        }
        if (!customText.matches("\\{[^}]+}")) {
            helpLinks.add(customText);
            return helpLinks;
        }
        if (selectable instanceof Html){
            return helpLinks;
        }
        Matcher matcher = pattern.matcher(customText);
        List<String> paths = new ArrayList<>();
        while (matcher.find()){
            String str = matcher.group().trim();
            if (!str.startsWith("$")){
                return helpLinks;
            }
            paths.add(str);
        }
        try(Stream<String> stream = paths.stream()){
            List<String> ps = stream.filter(p -> (!p.contains("*") && !p.contains(".."))).collect(Collectors.toList());
            for (String s:ps){
                Selectable jsonPath = selectable.jsonPath(s);
                if (jsonPath == null){
                    return helpLinks;
                }
                customText = customText.replaceAll("{\\s*" + s + "\\s*}", jsonPath.get());
            }
            ps = stream.filter(p -> p.contains("*")).collect(Collectors.toList());
            if (ps.size() <= 0){
                return helpLinks;
            }
            String path = ps.get(0);
            Selectable selVal = selectable.jsonPath(path);
            if (selVal == null){
                return helpLinks;
            }
            for (int i = 0; i < selVal.all().size(); i++) {
                String link = customText;
                for (String p:ps){
                    Selectable val = selectable.jsonPath(p.replace("*", String.valueOf(i)));
                    if (val == null){
                        break;
                    }
                    link = link.replaceAll("{\\s*" + p + "\\s*}", val.get());
                }
                if (!link.matches("\\{[^}]+}")){
                    helpLinks.add(link);
                }
            }
        }
       return helpLinks;
    }

    private void dealHelpSelect(Page page, PageConfig pageConfig) {
        List<HelpSelectConfig> helpSelect = pageConfig.getHelpSelect();
        if (helpSelect == null || helpSelect.size() <= 0) {
            return;
        }
        List<FieldSelectConfig> staticFields = pageConfig.getStaticFields();
        PageConfig nextPage = pageConfig.getNextHelpConfig();
        String configId = SecureUtil.md5(page.getRequest().getUrl()) + nextPage.getId();
        AbstractSelectable selectable = pageConfig.isJsonType() ? page.getJson() : page.getHtml();
        //新配置的静态字段
        List<FieldSelectConfig> newStaticFields = pageConfig.getDealStaticFields();
        for (FieldSelectConfig fConfig : staticFields) {
            if (fConfig.getSelectType() != SelectType.NONE) {
                pageConfig.setAllStaticFiles(false);
            }
            Selectable val = ProcessorUtils.getSelectVal(staticFields, selectable, fConfig);
            if (val != null) {
                FieldSelectConfig newFiled = new FieldSelectConfig(fConfig.getFiledName(), CollUtil.join(val.all(), ";"), SelectType.NONE);
                newStaticFields.add(newFiled);
            }
        }
        if (pageConfig.isAllStaticFiles()) {
            configId = nextPage.getId();
        }
        nextPage.getStaticFields().addAll(newStaticFields);
        this.siteConfig.getAllPageConfig().put(configId, nextPage);
        if (pageConfig.isJsonType()) {
            @SuppressWarnings("ConstantConditions")
            Json json = (Json) selectable;
            for (HelpSelectConfig hConfig : helpSelect) {
                SelectType selectType = hConfig.getSelectType();
                List<String> helpLinks = new ArrayList<>();
                switch (selectType) {
                    case JPATH:
                        helpLinks = fixHelpUrls(json.jsonPath(hConfig.getConfigText()).all(), page.getRequest().getUrl());
                        break;
                    case CUSTOM:
                        helpLinks = dealCustomHelpLink(newStaticFields, json, hConfig);
                        break;
                    default:
                        break;
                }
                addLinksToPage(helpLinks, configId, page);
            }
            return;
        }
        @SuppressWarnings("ConstantConditions")
        Html pageHtml = (Html) selectable;
        Selectable htmlLinks = pageHtml.links();
        for (HelpSelectConfig hConfig : helpSelect) {
            SelectType selectType = hConfig.getSelectType();
            List<String> helpLinks = new ArrayList<>();
            switch (selectType) {
                case CSS:
                    helpLinks = htmlLinks.$(hConfig.getConfigText()).all();
                    break;
                case REGEX:
                    helpLinks = htmlLinks.regex(hConfig.getConfigText()).all();
                    break;
                case XPATH:
                    helpLinks = htmlLinks.xpath(hConfig.getConfigText()).all();
                    break;
                case JPATH:
                    helpLinks = htmlLinks.jsonPath(hConfig.getConfigText()).all();
                    break;
                case CUSTOM:
                    helpLinks = dealCustomHelpLink(newStaticFields, pageHtml, hConfig);
                    break;
                default:
                    break;
            }
            addLinksToPage(helpLinks, configId, page);
        }
    }

    private List<String> fixHelpUrls(List<String> urls, String reqUrl) {
        if (urls == null) {
            return null;
        }
        List<String> links = new ArrayList<>();
        String domain = UrlUtils.getDomain(reqUrl);
        if (!domain.endsWith("/")) {
            domain += "/";
        }
        for (String url : urls) {
            if (url.startsWith("http")) {
                links.add(url);
            } else {
                links.add(domain + (url.startsWith("/") ? url.substring(1) : url));
            }
        }
        return links;
    }

    private List<String> dealCustomHelpLink(List<FieldSelectConfig> allStaticFields, AbstractSelectable selectable, HelpSelectConfig hConfig) {
        List<String> helpLinks = new ArrayList<>();
        String customText = hConfig.getConfigText();
        List<FieldSelectConfig> pathCombineMap = hConfig.getPathCombineMap();
        for (FieldSelectConfig fieldConfig : pathCombineMap) {
            Selectable val = ProcessorUtils.getSelectVal(allStaticFields, selectable, fieldConfig);
            if (val == null){
                continue;
            }
            customText = customText.replaceAll("{\\s*" + fieldConfig.getFiledName() + "\\s*}", val.get());
        }
        if (!customText.contains("{#")) {
            helpLinks.add(customText);
            return helpLinks;
        }
        Selectable totalVal = ProcessorUtils.getSelectVal(allStaticFields, selectable, hConfig.getTotalFieldSelect());
        if (totalVal != null) {
            String tVal = totalVal.get();
            if (StrUtil.isNotBlank(tVal) && tVal.matches("^\\d+$")) {
                long total = Long.parseLong(tVal);
                long pageNum = total / hConfig.getPageSize() + (total % hConfig.getPageSize() == 0 ? 0 : 1);
                for (int i = 0; i <= pageNum; i++) {
                    String url = customText;
                    url = url.replace("{#SIZE#}", String.valueOf(hConfig.getPageSize()))
                            .replace("{#PAGE#}", String.valueOf(i)).replace("{#OFFSET#}", String.valueOf(i));
                    helpLinks.add(url);
                }
            }
        }
        return helpLinks;
    }

    private void addLinksToPage(List<String> helpLinks, String configId, Page page) {
        if (helpLinks != null && helpLinks.size() > 0) {
            for (String lk : helpLinks) {
                Request request = new Request(lk);
                request.putExtra(SysConstant.URL_ID, configId);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public Site getSite() {
        return this.site;
    }
}
