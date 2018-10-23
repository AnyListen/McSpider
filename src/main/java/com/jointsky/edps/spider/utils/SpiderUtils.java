package com.jointsky.edps.spider.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import com.jointsky.edps.spider.config.SiteConfig;
import com.jointsky.edps.spider.filter.ValueFilter;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-18.
 */
public class SpiderUtils {
    public static Site buildSite(SiteConfig siteConfig){
        Site site = new Site();
        site.setDomain(siteConfig.getDomain());
        site.setCharset(siteConfig.getCharset());
        site.setUserAgent(siteConfig.getUserAgent());
        site.setUseGzip(siteConfig.isUseGzip());
        site.setCycleRetryTimes(siteConfig.getCycleRetryTimes());
        site.setRetryTimes(siteConfig.getRetryTimes());
        site.setRetrySleepTime(siteConfig.getRetrySleepTime());
        site.setTimeOut(siteConfig.getTimeOut());
        site.setSleepTime(siteConfig.getSleepTime());
        Map<String, String> header = siteConfig.getHeader();
        if (header == null){
            header = new HashMap<>();
        }
        if (StrUtil.isNotBlank(siteConfig.getCookie())){
            header.put("Cookie", siteConfig.getCookie());
        }
        header.forEach(site::addHeader);
        return site;
    }

    public static Downloader buildDownloader(SiteConfig siteConfig){
        try {
            Class<?> aClass = Class.forName(siteConfig.getDownloader());
            Object instance = aClass.newInstance();
            if (instance != null){
                return (Downloader)instance;
            }
        } catch (Exception e) {
            StaticLog.error(e);
        }
        return new HttpClientDownloader();
    }

    public static List<Pipeline> buildPipelines(SiteConfig siteConfig){
        List<Pipeline> pipelines = new ArrayList<>();
        List<String> strList = siteConfig.getPipelines();
        if (strList == null || strList.size() <= 0){
            pipelines.add(new ConsolePipeline());
            return pipelines;
        }
        strList.forEach(s-> {
            Pipeline pipeline = buildPipeline(s);
            if (pipeline != null){
                pipelines.add(pipeline);
            }
        });
        if (pipelines.size() <= 0){
            pipelines.add(new ConsolePipeline());
            return pipelines;
        }
        return pipelines;
    }

    public static Pipeline buildPipeline(String className){
        try {
            Class<?> aClass = Class.forName(className);
            Object instance = aClass.newInstance();
            if (instance != null){
                return (Pipeline)instance;
            }
        } catch (Exception e) {
            StaticLog.error(e);
        }
        return null;
    }

    public static Scheduler buildScheduler(SiteConfig siteConfig){
        try {
            Class<?> aClass = Class.forName(siteConfig.getScheduler());
            Object instance = aClass.newInstance();
            if (instance != null){
                return (Scheduler)instance;
            }
        } catch (Exception e) {
            StaticLog.error(e);
        }
        return new PriorityScheduler();
    }

    public static ValueFilter buildValueFilter(String str){
        try {
            Class<?> aClass = Class.forName(str);
            Object instance = aClass.newInstance();
            if (instance != null){
                return (ValueFilter)instance;
            }
        } catch (Exception e) {
            StaticLog.error(e);
        }
        return null;
    }
}
