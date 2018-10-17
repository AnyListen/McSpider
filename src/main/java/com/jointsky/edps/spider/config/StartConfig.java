package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.UrlType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class StartConfig extends PageConfig{

    private Map<String, Object> urls;

    public StartConfig(){
        super(UrlType.START);
        this.urls = new LinkedHashMap<>();
    }

    public Map<String, Object> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, Object> urls) {
        this.urls = urls;
    }
}
