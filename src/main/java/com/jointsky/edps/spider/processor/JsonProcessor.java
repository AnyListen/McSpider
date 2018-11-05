package com.jointsky.edps.spider.processor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public JsonProcessor(SiteConfig siteConfig) {
        this.site = SpiderUtils.buildSite(siteConfig);
    }

    @Override
    public void process(Page page) {
        Request request = page.getRequest();
        Object extra = request.getExtra(SysConstant.PAGE_SETTING);
        if (extra instanceof com.alibaba.fastjson.JSONObject){
            com.alibaba.fastjson.JSONObject extraJson = (com.alibaba.fastjson.JSONObject)extra;
            if (extraJson.containsKey("targetSelect")){
                extra = JSON.parseObject(JSON.toJSONString(extraJson), PageConfig.class);
            }
        }
        if (!(extra instanceof PageConfig)) {
            StaticLog.error("未知链接：" + request.getUrl());
            page.setSkip(true);
            return;
        }
        PageConfig pageConfig = ObjectUtil.clone((PageConfig) extra);
        if (page instanceof com.jointsky.edps.spider.common.Page){
            ((com.jointsky.edps.spider.common.Page)page).setExtractorConfig(pageConfig.getExtractorConfig());
        }
        AbstractSelectable selectable = pageConfig.isJsonType() ? page.getJson() : page.getHtml();
        dealStaticFields(selectable, pageConfig);
        dealHelpSelect(page, selectable, pageConfig);
        dealTargetSelect(page, selectable, pageConfig);
        dealTargetUrl(page, selectable, pageConfig);
    }

    /**
     * 处理静态字段里面的选择表达式
     * 删除无法正确匹配的表达式
     * 将其全部转换为 SelectType.NONE 类型
     */
    private void dealStaticFields(AbstractSelectable selectable, PageConfig pageConfig) {
        List<FieldSelectConfig> staticFields = pageConfig.getStaticFields();
        for (int i = 0; i < staticFields.size(); i++) {
            FieldSelectConfig fConfig = staticFields.get(i);
            if (fConfig.getSelectType() == SelectType.NONE) {
                continue;
            }
            Selectable val = ProcessorUtils.getSelectVal(staticFields, selectable, fConfig);
            if (val == null) {
                staticFields.remove(fConfig);
                i--;
            } else {
                fConfig.setConfigText(val.get());
                fConfig.setSelectType(SelectType.NONE);
            }
        }
    }

    /**
     * 处理结果页面
     * 根据 ResultFields 构建结果
     */
    private void dealTargetUrl(Page page, AbstractSelectable selectable, PageConfig pageConfig) {
        if (!pageConfig.isTargetUrl()){
            page.setSkip(true);
            return;
        }
        List<ResultSelectConfig> fieldSelect = pageConfig.getResultFields();
        if (fieldSelect == null || fieldSelect.size() <=0){
            page.setSkip(true);
            return;
        }
        String primaryKey = "";     //包含 * 的 JsonPath 作为循环的依据
        Map<String, List<String>> resultMap = new HashMap<>();
        List<FieldSelectConfig> staticFields = pageConfig.getStaticFields();
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
        //Html 类型的结果页或者 JsonPath 不包含 * 的，直接认为是最终结果
        //对 singleMap 中 value 的 size 为 1 的元素进行优化
        if (!pageConfig.isJsonType() || !primaryKey.contains("*")){
            Map<String, Object> singleMap = new HashMap<>();
            resultMap.forEach((k,v)->{
                if (v.size() == 0){
                    singleMap.put(k, null);
                }
                else if (v.size() == 1){
                    singleMap.put(k, v.get(0));
                }
                else{
                    singleMap.put(k, v);
                }
            });
            filterCheck(singleMap, page, pageConfig);
            return;
        }

        //处理包含 * 的结果
        //包含 * 意味着匹配到 jsonArray 的某一个节点，所以结果数量大于 1
        List<String> primaryValList = resultMap.get(primaryKey);
        int size = primaryValList.size();
        Map<String, Object> singleMap = new HashMap<>();
        //优化普通字段（value 的 size 的大小小于 primaryValList 的大小）
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
        //排除特殊状况
        if (!primaryKey.endsWith("*") &&  resultMap.entrySet().stream().filter(k-> k.getValue().size() >= size).count() <= 1){
            filterCheck(singleMap, page, pageConfig);
            return;
        }
        //循环生成结果
        for (int i = 0; i < size; i++) {
            if (StrUtil.isBlank(primaryValList.get(i))){
                continue;
            }
            if (primaryKey.endsWith("*") && !JSONUtil.isJson(primaryValList.get(i))){
                continue;
            }
            Map<String, Object> singleItem = new HashMap<>();
            singleMap.forEach(singleMap:: put);
            for (Map.Entry<String, List<String>> entry : resultMap.entrySet()) {
                String k = entry.getKey();
                List<String> v = entry.getValue();
                if (v.size() >= size) {
                    if (k.endsWith("*")) {
                        String s = v.get(i);
                        JSONObject jsonObject = JSONUtil.parseObj(s);
                        jsonObject.forEach(singleItem::put);
                    }
                    else{
                        singleItem.put(k, v.get(i));
                    }
                }
            }
            filterCheck(singleItem, page, pageConfig);
        }
    }

    /**
     * 对结果进行校验
     */
    private void filterCheck(Map<String, Object> resultMap, Page page, PageConfig pageConfig) {
        List<ResultSelectConfig> fieldSelect = pageConfig.getResultFields();
        for (ResultSelectConfig config : fieldSelect) {
            List<ValueFilterConfig> filters = config.getFilters();
            if (filters == null || filters.size() <= 0) {
                continue;
            }
            Object val = resultMap.getOrDefault(config.getFiledName(), null);
            for (ValueFilterConfig filterConfig : filters) {
                ValueFilter filter = SpiderUtils.buildValueFilter(filterConfig.getClassName());
                if (filter == null) {
                    continue;
                }
                filter.setValue(val);
                filter.setSettings(filterConfig.getSettingMap());
                if (filter.shouldRemove()) {
                    StaticLog.warn("字段 {} 不符合过滤规则：{}@{}：{}", config.getFiledName(),
                            filterConfig.getClassName(),
                            filterConfig.getSettingMap().get(SysConstant.SIMPLE_FILTER_METHOD),
                            resultMap
                    );
                    return;
                }
                val = filter.parseValue();
            }
            resultMap.put(config.getFiledName(), val);
        }
        page.putField(SysConstant.SINGLE_ITEM, resultMap);
    }

    /**
     * 提取目标页链接
     */
    private void dealTargetSelect(Page page, AbstractSelectable selectable, PageConfig pageConfig) {
        List<UrlSelectConfig> targetSelect = pageConfig.getTargetSelect();
        if (targetSelect == null || targetSelect.size() <= 0) {
            return;
        }
        List<FieldSelectConfig> staticFields = pageConfig.getStaticFields();
        PageConfig nextPage = pageConfig.getNextTargetConfig();
        if (staticFields != null && staticFields.size() > 0){
            nextPage.getStaticFields().addAll(staticFields);
        }
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
                        helpLinks = dealCustomTargetLink(staticFields, json, hConfig);
                        break;
                    default:
                        break;
                }
                addLinksToPage(helpLinks, nextPage, page);
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
                    helpLinks = dealCustomTargetLink(staticFields, pageHtml, hConfig);
                    break;
                default:
                    break;
            }
            addLinksToPage(helpLinks, nextPage, page);
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
            customText = customText.replaceAll("\\{\\s*" + fieldConfig.getFiledName() + "\\s*}", fieldConfig.getConfigText());
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
                customText = customText.replaceAll("\\{\\s*" + s + "\\s*}", jsonPath.get());
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
                    link = link.replaceAll("\\{\\s*" + p + "\\s*}", val.get());
                }
                if (!link.matches("\\{[^}]+}")){
                    helpLinks.add(link);
                }
            }
        }
       return helpLinks;
    }

    /**
     * 提取中间页链接
     */
    private void dealHelpSelect(Page page, AbstractSelectable selectable, PageConfig pageConfig) {
        List<HelpSelectConfig> helpSelect = pageConfig.getHelpSelect();
        if (helpSelect == null || helpSelect.size() <= 0) {
            return;
        }
        List<FieldSelectConfig> staticFields = pageConfig.getStaticFields();
        PageConfig nextPage = pageConfig.getNextHelpConfig();
        if (staticFields != null && staticFields.size() > 0){
            nextPage.getStaticFields().addAll(staticFields);
        }
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
                        helpLinks = dealCustomHelpLink(staticFields, json, hConfig);
                        break;
                    default:
                        break;
                }
                addLinksToPage(helpLinks, nextPage, page);
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
                    helpLinks = dealCustomHelpLink(staticFields, pageHtml, hConfig);
                    break;
                default:
                    break;
            }
            addLinksToPage(helpLinks, nextPage, page);
        }
    }

    /**
     * 修复不带 http 的链接
     */
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
        List<FieldSelectConfig> pathCombineParams = hConfig.getPathCombineParams();
        for (FieldSelectConfig fieldConfig : pathCombineParams) {
            Selectable val = ProcessorUtils.getSelectVal(allStaticFields, selectable, fieldConfig);
            if (val == null){
                continue;
            }
            customText = customText.replaceAll("\\{\\s*" + fieldConfig.getFiledName() + "\\s*}", val.get());
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

    private void addLinksToPage(List<String> helpLinks, PageConfig pageConfig,Page page) {
        if (helpLinks != null && helpLinks.size() > 0) {
            for (String lk : helpLinks) {
                Request request = new Request(lk);
                request.putExtra(SysConstant.PAGE_SETTING, pageConfig);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public Site getSite() {
        return this.site;
    }
}
