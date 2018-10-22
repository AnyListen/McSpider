package com.jointsky.edps.spider.filter;

import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-22.
 */
public class ValueFilter {
    private Object value;
    private Map<String, Object> settings;

    public ValueFilter(){}

    public boolean shouldRemove(){
        return false;
    }

    public Object parseValue(){
        return this.getValue();
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }
}
