package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.extractor.ExtractorConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class SiteConfig implements Serializable {
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
        this.downloader = "com.jointsky.edps.spider.downloader.DefaultDownloader";
        this.scheduler = "us.codecraft.webmagic.scheduler.PriorityScheduler";
    }

    public String getSiteId() {
        return siteId;
    }

    public SiteConfig setSiteId(String siteId) {
        this.siteId = siteId;
        return this;
    }

    public String getSiteName() {
        return siteName;
    }

    public SiteConfig setSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public SiteConfig setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public SiteConfig setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public int getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    public SiteConfig setCycleRetryTimes(int cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
        return this;
    }

    public int getRetrySleepTime() {
        return retrySleepTime;
    }

    public SiteConfig setRetrySleepTime(int retrySleepTime) {
        this.retrySleepTime = retrySleepTime;
        return this;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public SiteConfig setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public SiteConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public SiteConfig setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public SiteConfig setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String getCookie() {
        return cookie;
    }

    public SiteConfig setCookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public SiteConfig setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public boolean isUseGzip() {
        return useGzip;
    }

    public SiteConfig setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
        return this;
    }

    public String getProcessor() {
        return processor;
    }

    public SiteConfig setProcessor(String processor) {
        this.processor = processor;
        return this;
    }

    public List<String> getPipelines() {
        return pipelines;
    }

    public SiteConfig setPipelines(List<String> pipelines) {
        this.pipelines = pipelines;
        return this;
    }

    public String getDownloader() {
        return downloader;
    }

    public SiteConfig setDownloader(String downloader) {
        this.downloader = downloader;
        return this;
    }

    public String getScheduler() {
        return scheduler;
    }

    public SiteConfig setScheduler(String scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public StartConfig getStartPage() {
        return startPage;
    }

    public SiteConfig setStartPage(StartConfig startPage) {
        this.startPage = startPage;
        return this;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public SiteConfig setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    public boolean isExitWhenComplete() {
        return exitWhenComplete;
    }

    public SiteConfig setExitWhenComplete(boolean exitWhenComplete) {
        this.exitWhenComplete = exitWhenComplete;
        return this;
    }
}
