package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.SelectType;

import java.io.Serializable;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class UrlSelectConfig implements Serializable {
    private boolean jsonType;
    private String configText;
    private SelectType selectType;

    public UrlSelectConfig(String configText, SelectType selectType) {
        this.jsonType = false;
        this.configText = configText;
        this.selectType = selectType;
    }

    public UrlSelectConfig(boolean jsonType, String configText, SelectType selectType) {
        this.jsonType = jsonType;
        this.configText = configText;
        this.selectType = selectType;
    }

    public boolean isJsonType() {
        return jsonType;
    }

    public void setJsonType(boolean jsonType) {
        this.jsonType = jsonType;
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
