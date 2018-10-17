package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.SelectType;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class FieldSelectConfig {
    private String filedName;
    private String configText;
    private SelectType selectType;

    public FieldSelectConfig(){}

    public FieldSelectConfig(String filedName, String configText, SelectType selectType) {
        this.filedName = filedName;
        this.configText = configText;
        this.selectType = selectType;
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getConfigText() {
        return configText;
    }

    public void setConfigText(String configText) {
        this.configText = configText;
    }

    public SelectType getSelectType() {
        return selectType;
    }

    public void setSelectType(SelectType selectType) {
        this.selectType = selectType;
    }
}
