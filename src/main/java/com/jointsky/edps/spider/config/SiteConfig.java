package com.jointsky.edps.spider.config;

import us.codecraft.webmagic.Site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class SiteConfig {
    private Map<String, PageConfig> allPageConfig;
    private String siteId;
    private String siteName;
    private Site site;
    private String processor;
    private List<String> pipelines;
    private String downloader;
    private String scheduler;
    private StartConfig startPage;

    public SiteConfig(String siteId, String siteName) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.pipelines = new ArrayList<>();
        this.allPageConfig = new HashMap<>();
        this.downloader = "us.codecraft.webmagic.downloader.HttpClientDownloader";
        this.scheduler = "us.codecraft.webmagic.scheduler.PriorityScheduler";
    }

    public Map<String, PageConfig> getAllPageConfig() {
        return allPageConfig;
    }

    public void setAllPageConfig(Map<String, PageConfig> allPageConfig) {
        this.allPageConfig = allPageConfig;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<String> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<String> pipelines) {
        this.pipelines = pipelines;
    }

    public String getDownloader() {
        return downloader;
    }

    public void setDownloader(String downloader) {
        this.downloader = downloader;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public StartConfig getStartPage() {
        return startPage;
    }

    public void setStartPage(StartConfig startPage) {
        this.startPage = startPage;
    }
}
