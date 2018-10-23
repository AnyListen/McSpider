package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.SysConstant;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class StartConfig extends PageConfig implements Serializable {

    private Map<String, Object> urls;

    public StartConfig(){
        super(SysConstant.START_URL);
        this.urls = new LinkedHashMap<>();
    }

    public Map<String, Object> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, Object> urls) {
        this.urls = urls;
    }
}
