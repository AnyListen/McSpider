package com.jointsky.edps.spider.processor;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.jointsky.edps.spider.common.SelectType;
import com.jointsky.edps.spider.common.SysConstant;
import com.jointsky.edps.spider.config.HelpSelectConfig;
import com.jointsky.edps.spider.config.PageConfig;
import com.jointsky.edps.spider.config.SiteConfig;
import com.jointsky.edps.spider.config.StartConfig;
import com.jointsky.edps.spider.utils.SpiderUtils;
import org.junit.Test;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

import javax.jws.Oneway;
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
        startConfig.getHelpSelect().add(new HelpSelectConfig(false, "www.xinhuanet.com/politics/leaders/\\w+/index.htm", SelectType.REGEX));
        PageConfig leaderHelpPage = new PageConfig("leader-page");
        leaderHelpPage.getHelpSelect().add(new HelpSelectConfig(false, "www.xinhuanet.com/politics/leaders/\\w+/jhqw.htm", SelectType.REGEX));
        leaderHelpPage.getHelpSelect().add(new HelpSelectConfig(false, "www.xinhuanet.com/politics/leaders/\\w+/kcsc.htm", SelectType.REGEX));
        leaderHelpPage.getHelpSelect().add(new HelpSelectConfig(false, "www.xinhuanet.com/politics/leaders/\\w+/hyhd.htm", SelectType.REGEX));

        startConfig.setNextHelpConfig();


        SiteConfig siteConfig = new SiteConfig("xh-leader", "新华网-领导讲话");
        siteConfig.setCharset("UTF-8").setExitWhenComplete(true).setStartPage(startConfig);
        Map<String, Object> startUrls = siteConfig.getStartPage().getUrls();
        if (startUrls == null || startUrls.size() <=0){
            StaticLog.error("请设置起始页！");
            return;
        }
        List<Request> requestList = new ArrayList<>();
        startUrls.forEach((k,v)->{
            Request request = new Request(k);
            request.putExtra(SysConstant.URL_ID, SysConstant.START_URL);
            requestList.add(request);
        });

        Spider spider = Spider.create(new JsonProcessor(siteConfig))
                .setDownloader(SpiderUtils.buildDownloader(siteConfig))
                .setPipelines(SpiderUtils.buildPipelines(siteConfig))
                .setScheduler(SpiderUtils.buildScheduler(siteConfig))
                .startRequest(requestList)
                .setUUID(siteConfig.getSiteId())
                .thread(siteConfig.getThreadNum())
                .setExitWhenComplete(siteConfig.isExitWhenComplete());
        spider.runAsync();
    }

    @Test
    public void testJsonSel(){
        HttpClientDownloader downloader = new HttpClientDownloader();
        Html html = downloader.download("http://hot.news.cntv.cn/api/Content/contentinfo?id=ARTIeYJeezpb2BVas5XYHGBY161013");
        Json json = new Json(html.getDocument().text());
        Selectable jsonPath = json.jsonPath("$.hotList.itemList.*");
        List<String> all = jsonPath.all();
        System.out.println(all);
    }
}