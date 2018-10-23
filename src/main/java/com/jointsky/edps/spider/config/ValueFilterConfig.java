package com.jointsky.edps.spider.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-23.
 */
public class ValueFilterConfig implements Serializable {
    private String className;
    private Map<String, Object> settingMap = new HashMap<>();

    public String getClassName() {
        return className;
    }

    public ValueFilterConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public Map<String, Object> getSettingMap() {
        return settingMap;
    }

    public ValueFilterConfig setSettingMap(Map<String, Object> settingMap) {
        this.settingMap = settingMap;
        return this;
    }

    public ValueFilterConfig addSetting(String key, Object value){
        this.settingMap.put(key, value);
        return this;
    }
}
