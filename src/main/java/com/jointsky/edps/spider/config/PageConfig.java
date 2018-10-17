package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.UrlType;

import java.util.List;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class PageConfig {
    /**
     * URL 类型
     */
    private UrlType urlType;
    /**
     * 结果是否为 JSON
     */
    private boolean jsonType;
    /**
     * 是否为结果页
     */
    private boolean targetUrl;
    /**
     * 结果页选择器
     */
    private List<UrlSelectConfig> targetSelect;
    /**
     * 中间页选择器
     */
    private List<HelpSelectConfig> helpSelect;
    /**
     * 字段选择器
     */
    private List<FieldSelectConfig> fieldSelect;
    /**
     * 静态字段
     */
    private List<FieldSelectConfig> staticFields;

    public UrlType getUrlType() {
        return urlType;
    }

    public void setUrlType(UrlType urlType) {
        this.urlType = urlType;
    }

    public boolean isJsonType() {
        return jsonType;
    }

    public void setJsonType(boolean jsonType) {
        this.jsonType = jsonType;
    }

    public boolean isTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(boolean targetUrl) {
        this.targetUrl = targetUrl;
    }

    public List<UrlSelectConfig> getTargetSelect() {
        return targetSelect;
    }

    public void setTargetSelect(List<UrlSelectConfig> targetSelect) {
        this.targetSelect = targetSelect;
    }

    public List<HelpSelectConfig> getHelpSelect() {
        return helpSelect;
    }

    public void setHelpSelect(List<HelpSelectConfig> helpSelect) {
        this.helpSelect = helpSelect;
    }

    public List<FieldSelectConfig> getFieldSelect() {
        return fieldSelect;
    }

    public void setFieldSelect(List<FieldSelectConfig> fieldSelect) {
        this.fieldSelect = fieldSelect;
    }

    public List<FieldSelectConfig> getStaticFields() {
        return staticFields;
    }

    public void setStaticFields(List<FieldSelectConfig> staticFields) {
        this.staticFields = staticFields;
    }
}
