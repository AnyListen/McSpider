package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.SelectType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class HelpSelectConfig extends UrlSelectConfig implements Serializable {
    private int pageSize = 20;
    private FieldSelectConfig totalFieldSelect;
    private List<FieldSelectConfig> pathCombineParams;

    public HelpSelectConfig(boolean jsonType, String configText, SelectType selectType) {
        super(jsonType, configText, selectType);
        this.pathCombineParams = new ArrayList<>();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<FieldSelectConfig> getPathCombineParams() {
        return pathCombineParams;
    }

    public void setPathCombineParams(List<FieldSelectConfig> pathCombineParams) {
        this.pathCombineParams = pathCombineParams;
    }

    public FieldSelectConfig getTotalFieldSelect() {
        return totalFieldSelect;
    }

    public void setTotalFieldSelect(FieldSelectConfig totalFieldSelect) {
        this.totalFieldSelect = totalFieldSelect;
    }
}
