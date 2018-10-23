package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.SelectType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-22.
 */
public class ResultSelectConfig extends FieldSelectConfig implements Serializable {
    private List<ValueFilterConfig> filters = new ArrayList<>();

    public ResultSelectConfig() {
        super();
    }

    public ResultSelectConfig(String filedName, String configText, SelectType selectType) {
        super(filedName, configText, selectType);
    }

    public ResultSelectConfig(String filedName, String configText, SelectType selectType, int group) {
        super(filedName, configText, selectType, group);
    }

    public List<ValueFilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(List<ValueFilterConfig> filters) {
        this.filters = filters;
    }
}
