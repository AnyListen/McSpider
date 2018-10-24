package com.jointsky.edps.spider.extractor;

import com.jointsky.edps.spider.config.FieldSelectConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-24.
 */
public class ExtractorConfig {
    private FieldSelectConfig titleSelect;
    private List<FieldSelectConfig> summarySelect;
    private List<FieldSelectConfig> keywordSelect;
    private List<FieldSelectConfig> htmlSelect;
    private List<FieldSelectConfig> imgSelect;
    private FieldSelectConfig timeSelect;

    public ExtractorConfig(){
        this.summarySelect = new ArrayList<>();
        this.keywordSelect = new ArrayList<>();
        this.htmlSelect = new ArrayList<>();
        this.imgSelect = new ArrayList<>();
    }

    public FieldSelectConfig getTitleSelect() {
        return titleSelect;
    }

    public ExtractorConfig setTitleSelect(FieldSelectConfig titleSelect) {
        this.titleSelect = titleSelect;
        return this;
    }

    public List<FieldSelectConfig> getSummarySelect() {
        return summarySelect;
    }

    public ExtractorConfig setSummarySelect(List<FieldSelectConfig> summarySelect) {
        this.summarySelect = summarySelect;
        return this;
    }

    public List<FieldSelectConfig> getKeywordSelect() {
        return keywordSelect;
    }

    public ExtractorConfig setKeywordSelect(List<FieldSelectConfig> keywordSelect) {
        this.keywordSelect = keywordSelect;
        return this;
    }

    public List<FieldSelectConfig> getHtmlSelect() {
        return htmlSelect;
    }

    public ExtractorConfig setHtmlSelect(List<FieldSelectConfig> htmlSelect) {
        this.htmlSelect = htmlSelect;
        return this;
    }

    public List<FieldSelectConfig> getImgSelect() {
        return imgSelect;
    }

    public ExtractorConfig setImgSelect(List<FieldSelectConfig> imgSelect) {
        this.imgSelect = imgSelect;
        return this;
    }

    public FieldSelectConfig getTimeSelect() {
        return timeSelect;
    }

    public ExtractorConfig setTimeSelect(FieldSelectConfig timeSelect) {
        this.timeSelect = timeSelect;
        return this;
    }
}
