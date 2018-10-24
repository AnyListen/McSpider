package com.jointsky.edps.spider.extractor;

import java.util.ArrayList;
import java.util.List;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-19.
 */
public class HtmlArticle {
    private String id;
    private String url;
    private String title;
    private String time;
    private String content;
    private String html;
    private String simHash;
    private List<String> keyword;
    private List<String> phrase;
    private String summary;
    private List<String> images;


    public HtmlArticle(){
        this.keyword = new ArrayList<>();
        this.phrase = new ArrayList<>();
        this.images = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getSimHash() {
        return simHash;
    }

    public void setSimHash(String simHash) {
        this.simHash = simHash;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    public List<String> getPhrase() {
        return phrase;
    }

    public void setPhrase(List<String> phrase) {
        this.phrase = phrase;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
