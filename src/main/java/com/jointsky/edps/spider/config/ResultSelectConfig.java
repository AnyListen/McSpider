package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.SelectType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-22.
 */
public class ResultSelectConfig extends FieldSelectConfig implements Serializable {
    private List<HashMap.SimpleEntry<String, Map<String, Object>>> filters;

    public ResultSelectConfig() {
        super();
    }

    public ResultSelectConfig(String filedName, String configText, SelectType selectType) {
        super(filedName, configText, selectType);
    }

    public ResultSelectConfig(String filedName, String configText, SelectType selectType, int group) {
        super(filedName, configText, selectType, group);
    }

    public List<HashMap.SimpleEntry<String, Map<String, Object>>> getFilters() {
        return filters;
    }

    public void setFilters(List<HashMap.SimpleEntry<String, Map<String, Object>>> filters) {
        this.filters = filters;
    }
}
