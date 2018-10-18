package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.SelectType;

import java.util.ArrayList;
import java.util.List;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class HelpSelectConfig extends UrlSelectConfig{
    private int pageSize = 20;
    private FieldSelectConfig totalFieldSelect;
    private List<FieldSelectConfig> pathCombineMap;

    public HelpSelectConfig(boolean jsonType, String configText, SelectType selectType) {
        super(jsonType, configText, selectType);
        this.pathCombineMap = new ArrayList<>();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<FieldSelectConfig> getPathCombineMap() {
        return pathCombineMap;
    }

    public void setPathCombineMap(List<FieldSelectConfig> pathCombineMap) {
        this.pathCombineMap = pathCombineMap;
    }

    public FieldSelectConfig getTotalFieldSelect() {
        return totalFieldSelect;
    }

    public void setTotalFieldSelect(FieldSelectConfig totalFieldSelect) {
        this.totalFieldSelect = totalFieldSelect;
    }
}
