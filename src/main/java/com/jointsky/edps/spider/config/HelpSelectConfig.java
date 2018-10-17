package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.SelectType;

import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class HelpSelectConfig extends UrlSelectConfig{
    private String totalSelect;
    private SelectType totalSelectType;
    private int pageSize;
    private Map<String,Map<String, SelectType>> pathCombineMap;

    public String getTotalSelect() {
        return totalSelect;
    }

    public void setTotalSelect(String totalSelect) {
        this.totalSelect = totalSelect;
    }

    public SelectType getTotalSelectType() {
        return totalSelectType;
    }

    public void setTotalSelectType(SelectType totalSelectType) {
        this.totalSelectType = totalSelectType;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Map<String, Map<String, SelectType>> getPathCombineMap() {
        return pathCombineMap;
    }

    public void setPathCombineMap(Map<String, Map<String, SelectType>> pathCombineMap) {
        this.pathCombineMap = pathCombineMap;
    }
}
