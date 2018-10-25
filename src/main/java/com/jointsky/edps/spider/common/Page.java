package com.jointsky.edps.spider.common;

import com.jointsky.edps.spider.extractor.ExtractorConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-25.
 */
public class Page extends us.codecraft.webmagic.Page {

    private static final Pattern jsonPattern = Pattern.compile("^\\s*\\w+\\s*\\(([\\s\\S]+?)\\s*\\)\\s*$");

    @Override
    public us.codecraft.webmagic.Page setRawText(String rawText) {
        Matcher matcher = jsonPattern.matcher(rawText);
        if (matcher.find()){
            rawText = matcher.group(1);
        }
        return super.setRawText(rawText);
    }

    @Override
    public void setDownloadSuccess(boolean downloadSuccess) {
        super.setDownloadSuccess(downloadSuccess);
    }

    public static Page fail() {
        Page page = new Page();
        page.setDownloadSuccess(false);
        return page;
    }

    private Html html;
    @Override
    public Html getHtml() {
        if (this.html == null) {
            this.html = new Html(super.getRawText(), super.getRequest().getUrl());
            this.html.setConfig(extractorConfig);
            super.getHtml();
        }
        return this.html;
    }

    private ExtractorConfig extractorConfig;
    public Page setExtractorConfig(ExtractorConfig config){
        this.extractorConfig = config;
        return this;
    }
}
