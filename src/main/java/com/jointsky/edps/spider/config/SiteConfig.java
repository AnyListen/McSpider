package com.jointsky.edps.spider.config;

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
    private int sleepTime = 1000;
    private int retryTimes = 2;
    private int cycleRetryTimes = 0;
    private int retrySleepTime = 5000;
    private int timeOut = 10000;
    private int threadNum = 1;

    private String domain;
    private String charset;
    private String userAgent;
    private String cookie;
    private Map<String, String> header;
    private boolean useGzip = true;
    private boolean exitWhenComplete = true;

    private String processor;
    private List<String> pipelines;
    private String downloader;
    private String scheduler;
    private StartConfig startPage;

    public SiteConfig(String siteId, String siteName) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.pipelines = new ArrayList<>();
        this.header = new HashMap<>();
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

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    public void setCycleRetryTimes(int cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
    }

    public int getRetrySleepTime() {
        return retrySleepTime;
    }

    public void setRetrySleepTime(int retrySleepTime) {
        this.retrySleepTime = retrySleepTime;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public boolean isUseGzip() {
        return useGzip;
    }

    public void setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
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

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public boolean isExitWhenComplete() {
        return exitWhenComplete;
    }

    public void setExitWhenComplete(boolean exitWhenComplete) {
        this.exitWhenComplete = exitWhenComplete;
    }
}
