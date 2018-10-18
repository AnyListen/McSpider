package com.jointsky.edps.spider.processor;

import cn.hutool.log.StaticLog;
import com.jointsky.edps.spider.common.SysConstant;
import com.jointsky.edps.spider.config.SiteConfig;
import com.jointsky.edps.spider.utils.SpiderUtils;
import org.junit.Test;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
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
        SiteConfig siteConfig = new SiteConfig("xh-leader", "新华网-领导讲话");
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
}