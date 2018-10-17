package com.jointsky.edps.spider.config;

import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class StartConfig extends PageConfig{
    private Map<String, Object> urls;

    public Map<String, Object> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, Object> urls) {
        this.urls = urls;
    }
}
