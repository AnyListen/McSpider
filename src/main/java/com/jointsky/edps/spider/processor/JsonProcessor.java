package com.jointsky.edps.spider.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.log.StaticLog;
import com.jointsky.edps.spider.common.SelectType;
import com.jointsky.edps.spider.common.SysConstant;
import com.jointsky.edps.spider.config.FieldSelectConfig;
import com.jointsky.edps.spider.config.HelpSelectConfig;
import com.jointsky.edps.spider.config.PageConfig;
import com.jointsky.edps.spider.config.SiteConfig;
import com.jointsky.edps.spider.utils.ProcessorUtils;
import com.jointsky.edps.spider.utils.SpiderUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

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
        if (extra == null){
            StaticLog.error("未知链接：" + request.getUrl());
            page.setSkip(true);
            return;
        }
        String urlId = extra.toString();
        PageConfig pageConfig = this.siteConfig.getAllPageConfig().get(urlId);
        dealHelpSelect(page, pageConfig);
        dealTargetSelect(page, pageConfig);
    }

    private void dealTargetSelect(Page page, PageConfig pageConfig) {

    }

    private void dealHelpSelect(Page page, PageConfig pageConfig) {
        List<HelpSelectConfig> helpSelect = pageConfig.getHelpSelect();
        if (helpSelect == null || helpSelect.size() <= 0){
            return;
        }
        if (pageConfig.isJsonType()){

        }
        else {
            Html pageHtml = page.getHtml();
            //新配置的静态字段
            List<FieldSelectConfig> newStaticFields = new ArrayList<>();
            pageConfig.getStaticFields().forEach(fConfig -> {
                Selectable val = ProcessorUtils.getSelectVal(pageHtml, fConfig);
                if (val != null) {
                    FieldSelectConfig newFiled = new FieldSelectConfig(fConfig.getFiledName(), CollUtil.join(val.all(), ";"), SelectType.NONE);
                    newStaticFields.add(newFiled);
                }
            });
            PageConfig nextPage = pageConfig.getNextPage();
            nextPage.getStaticFields().addAll(newStaticFields);
            String configId = SecureUtil.md5(page.getRequest().getUrl()) + nextPage.getId();
            Selectable htmlLinks = pageHtml.links();
            helpSelect.forEach(hConfig -> {
                SelectType selectType = hConfig.getSelectType();
                List<String> helpLinks;
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
                        helpLinks = page.getJson().jsonPath(hConfig.getConfigText()).all();
                        break;
                    case CUSTOM:
                        //?userId={uid}&page={#PAGE#}&size={#LIMIT#}&offset={#OFFSET#}
                        String customText = hConfig.getConfigText();
                        List<FieldSelectConfig> pathCombineMap = hConfig.getPathCombineMap();
                        for (FieldSelectConfig fieldConfig : pathCombineMap) {
                            Selectable val = ProcessorUtils.getSelectVal(pageHtml, fieldConfig);
                            if (val == null){
                                continue;
                            }
                            customText = customText.replaceAll("{\\s*" + fieldConfig.getFiledName() + "\\s*}", val.get());
                        }
                        Selectable totalVal = ProcessorUtils.getSelectVal(pageHtml, hConfig.getTotalFieldSelect());
                        helpLinks = new ArrayList<>();
                        if(totalVal != null){
                            String tVal = totalVal.get();
                            if (StrUtil.isNotBlank(tVal) && tVal.matches("^\\d+$")){
                                long total = Long.parseLong(tVal);
                                long pageNum = total/ hConfig.getPageSize() + (total% hConfig.getPageSize()==0? 0 : 1);
                                for (int i = 0; i <= pageNum; i++) {
                                    String url = customText;
                                    url = url.replace("{#SIZE#}", String.valueOf(hConfig.getPageSize()))
                                            .replace("{#PAGE#}", String.valueOf(i)).replace("{#OFFSET#}", String.valueOf(i));
                                    helpLinks.add(url);
                                }
                            }
                            else{
                                helpLinks.add(customText);
                            }
                        }
                        else{
                            helpLinks.add(customText);
                        }
                        break;
                    default:
                        helpLinks = new ArrayList<>();
                        break;
                }

                helpLinks.forEach(lk->{
                    Request request = new Request(lk);
                    request.putExtra(SysConstant.URL_ID, configId);
                    page.addTargetRequest(request);
                });
            });
        }





    }

    @Override
    public Site getSite() {
        return this.site;
    }
}
