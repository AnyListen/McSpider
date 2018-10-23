package com.jointsky.edps.spider.processor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.StaticLog;
import com.jointsky.edps.spider.common.SelectType;
import com.jointsky.edps.spider.config.*;
import com.jointsky.edps.spider.utils.SpiderUtils;
import org.junit.Test;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-18.
 */
public class JsonProcessorTest {

    @Test
    public void process() {
        StartConfig startConfig = new StartConfig();
        Map<String, Object> startUrlMap = new HashMap<>();
        startUrlMap.put("http://www.xinhuanet.com/politics/leaders/index.htm", null);
        startConfig.setUrls(startUrlMap);
        startConfig.getHelpSelect().add(new HelpSelectConfig(false, "http://www.xinhuanet.com/politics/leaders/\\w+/index.htm", SelectType.REGEX));
        PageConfig leaderHelpPage = new PageConfig();
        leaderHelpPage.getHelpSelect().add(new HelpSelectConfig(false, "http://www.xinhuanet.com/politics/leaders/\\w+/jhqw.htm", SelectType.REGEX));
        leaderHelpPage.getHelpSelect().add(new HelpSelectConfig(false, "http://www.xinhuanet.com/politics/leaders/\\w+/kcsc.htm", SelectType.REGEX));
        leaderHelpPage.getHelpSelect().add(new HelpSelectConfig(false, "http://www.xinhuanet.com/politics/leaders/\\w+/hyhd.htm", SelectType.REGEX));

        //region 结果页面内容提取
        PageConfig detailHtmlPage = new PageConfig();
        detailHtmlPage.setTargetUrl(true);
        detailHtmlPage.getResultFields().add(new ResultSelectConfig("leader_nm", "leader_nm", SelectType.FIELD));
        ResultSelectConfig titleField = new ResultSelectConfig("title", "(?<=h-title\">\\s{0,30})[^<]+?(?=\\s*</div>)", SelectType.REGEX);
//        HashMap<String, Object> titleFilterSetting = new HashMap<String, Object>() {{
//            put(SysConstant.SIMPLE_FILTER_METHOD, SysConstant.EMPTY_FILTER);
//        }};
//        ValueFilterConfig titleFilter = new ValueFilterConfig().setClassName("com.jointsky.edps.spider.filter.SimpleValueFilter").setSettingMap(titleFilterSetting);
//        titleField.getFilters().add(titleFilter);
        detailHtmlPage.getResultFields().add(titleField);
        detailHtmlPage.getResultFields().add(new ResultSelectConfig("summary", "(?<=description\" content=\")[^\"]+?(?=\")", SelectType.REGEX));
        PageConfig detailJsonPage = ObjectUtil.clone(detailHtmlPage);
        detailHtmlPage.setTargetUrl(true);
        //endregion

        HelpSelectConfig leaderJsonHelpSelect = new HelpSelectConfig(true, "http://qc.wa.news.cn/nodeart/list?nid={nid}&pgnum=1&cnt=10&attr=&tp=1&orderby=1&callback=jQuery1&_=1540199776", SelectType.CUSTOM);
        FieldSelectConfig nidSelect = new FieldSelectConfig("nid", "getid\">\\s*(\\d+)", SelectType.REGEX);
        nidSelect.setGroup(1);
        leaderJsonHelpSelect.getPathCombineParams().add(nidSelect);

        PageConfig jsonTargetPage = new PageConfig();
        jsonTargetPage.setJsonType(true);
        jsonTargetPage.getTargetSelect().add(new UrlSelectConfig("$.data.list.*.LinkUrl", SelectType.JPATH));

        HelpSelectConfig jsonHelpSelect = new HelpSelectConfig(true, "http://qc.wa.news.cn/nodeart/list?nid={nid}&pgnum={#PAGE#}&cnt={#SIZE#}&attr=&tp=1&orderby=1&callback=jQuery1&_=1540199776", SelectType.CUSTOM);
        jsonHelpSelect.getPathCombineParams().add(new FieldSelectConfig("nid", "$.data.list[0].NodeId", SelectType.JPATH));
        jsonHelpSelect.setPageSize(10);
        jsonHelpSelect.setTotalFieldSelect(new FieldSelectConfig("page", "$.totalnum", SelectType.JPATH));
        jsonTargetPage.getHelpSelect().add(jsonHelpSelect);
        jsonTargetPage.setNextTargetConfig(detailJsonPage);

        PageConfig jsonNextHelp = ObjectUtil.clone(jsonTargetPage);
        jsonNextHelp.setHelpSelect(new ArrayList<>());
        jsonTargetPage.setNextHelpConfig(jsonNextHelp);


        PageConfig targetHtmlPage = new PageConfig();
        targetHtmlPage.getTargetSelect().add(new HelpSelectConfig(false, "http://www.xinhuanet.com/politics/leaders/[\\w/]+?/c_\\d+\\.htm", SelectType.REGEX));
        targetHtmlPage.getHelpSelect().add(leaderJsonHelpSelect);
        targetHtmlPage.setNextTargetConfig(detailHtmlPage);
        targetHtmlPage.getStaticFields().add(new FieldSelectConfig("leader_nm", "(?<=<title>\\s{0,30})\\S+?(?=报道)", SelectType.REGEX));
        targetHtmlPage.setNextHelpConfig(jsonTargetPage);


        leaderHelpPage.setNextHelpConfig(targetHtmlPage);
        startConfig.setNextHelpConfig(leaderHelpPage);


        SiteConfig siteConfig = new SiteConfig("xh-leader", "新华网-领导讲话");
        siteConfig.setCharset("UTF-8").setExitWhenComplete(true).setStartPage(startConfig);
        Map<String, Object> startUrls = siteConfig.getStartPage().getUrls();
        if (startUrls == null || startUrls.size() <=0){
            StaticLog.error("请设置起始页！");
            return;
        }
        Spider spider = SpiderUtils.buildJsonSpider(siteConfig);
        spider.run();
    }

    @Test
    public void testJsonSel(){
        HttpClientDownloader downloader = new HttpClientDownloader();
        Html html = downloader.download("http://hot.news.cntv.cn/api/Content/contentinfo?id=ARTIeYJeezpb2BVas5XYHGBY161013");
        Json json = new Json(html.getDocument().text());
        Selectable jsonPath = json.jsonPath("$.hotList.itemList[0].detailUrl");
        List<String> all = jsonPath.all();
        System.out.println(all);
    }
}