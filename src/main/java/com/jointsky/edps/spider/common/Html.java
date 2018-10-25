package com.jointsky.edps.spider.common;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HtmlUtil;
import com.jointsky.edps.spider.config.FieldSelectConfig;
import com.jointsky.edps.spider.extractor.ContentExtractor;
import com.jointsky.edps.spider.extractor.ExtractorConfig;
import com.jointsky.edps.spider.extractor.HtmlArticle;
import com.jointsky.edps.spider.utils.ProcessorUtils;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.ArrayList;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-25.
 */
public class Html extends us.codecraft.webmagic.selector.Html {
    private HtmlArticle htmlArticle;
    private ExtractorConfig config;

    public Html(String text, String url) {
        super(text, url);
    }

    public Html(String text) {
        super(text);
    }

    public Html(Document document) {
        super(document);
    }

    private void initArticle(){
        if (this.htmlArticle == null){
            this.htmlArticle = getArticle();
        }
    }

    public Selectable article(String filedName){
        initArticle();
        Selectable selectable = null;
        switch (filedName.toUpperCase()){
            case SysConstant.TITLE:
                selectable = new PlainText(htmlArticle.getTitle());
                break;
            case SysConstant.TIME:
                selectable = new PlainText(htmlArticle.getTime());
                break;
            case SysConstant.SUMMARY:
                selectable = new PlainText(htmlArticle.getSummary());
                break;
            case SysConstant.KEYWORD:
                selectable = new PlainText(htmlArticle.getKeyword());
                break;
            case SysConstant.IMAGE:
                selectable = new PlainText(htmlArticle.getImages());
                break;
            case SysConstant.HTML:
                selectable = new PlainText(htmlArticle.getHtml());
                break;
            case SysConstant.CONTENT:
                selectable = new PlainText(htmlArticle.getContent());
                break;
            case SysConstant.ID:
                selectable = new PlainText(htmlArticle.getId());
                break;
            case SysConstant.URL:
                selectable = new PlainText(htmlArticle.getUrl());
                break;
            case SysConstant.DOMAIN:
                selectable = new PlainText(htmlArticle.getDomain());
                break;
            default:
                break;
        }
        return selectable;
    }

    public static Html create(String text) {
        return new Html(text);
    }

    public static Html create(String text, String url) {
        return new Html(text, url);
    }

    private ExtractorConfig initExtractor(ExtractorConfig extractorConfig){
        if (extractorConfig == null){
            extractorConfig = new ExtractorConfig();
        }
        if (extractorConfig.getTitleSelect() == null){
            extractorConfig.setTitleSelect(new FieldSelectConfig("title", SysConstant.TITLE, SelectType.ARTICLE));
        }
        if (extractorConfig.getTimeSelect() == null){
            extractorConfig.setTimeSelect(new FieldSelectConfig("time", SysConstant.TIME, SelectType.ARTICLE));
        }
        if (extractorConfig.getHtmlSelect() == null || extractorConfig.getHtmlSelect().size() <= 0){
            extractorConfig.setHtmlSelect(new ArrayList<FieldSelectConfig>(){{
                add(new FieldSelectConfig("html", SysConstant.HTML, SelectType.ARTICLE));
            }});
        }
        if (extractorConfig.getSummarySelect() == null || extractorConfig.getSummarySelect().size() <= 0){
            extractorConfig.setSummarySelect(new ArrayList<FieldSelectConfig>(){{
                add(new FieldSelectConfig("summary", SysConstant.SUMMARY, SelectType.ARTICLE));
            }});
        }
        if (extractorConfig.getKeywordSelect() == null || extractorConfig.getKeywordSelect().size() <= 0){
            extractorConfig.setKeywordSelect(new ArrayList<FieldSelectConfig>(){{
                add(new FieldSelectConfig("keyword", SysConstant.KEYWORD, SelectType.ARTICLE));
            }});
        }
        if (extractorConfig.getImgSelect() == null || extractorConfig.getImgSelect().size() <= 0){
            extractorConfig.setImgSelect(new ArrayList<FieldSelectConfig>(){{
                add(new FieldSelectConfig("image", SysConstant.IMAGE, SelectType.ARTICLE));
            }});
        }
        return extractorConfig;
    }

    public ExtractorConfig getConfig() {
        return config;
    }

    public void setConfig(ExtractorConfig config) {
        this.config = config;
    }

    private HtmlArticle defaultArticle;
    private void initDefaultArticle() {
        if (this.defaultArticle == null){
            this.defaultArticle = ContentExtractor.getArticle(this);
        }
    }

    public HtmlArticle getArticle(){
        HtmlArticle htmlArticle = new HtmlArticle();
        this.config = initExtractor(this.config);
        String baseUri = this.getDocument().baseUri();
        if (StrUtil.isNotBlank(baseUri) && baseUri.startsWith("http")){
            htmlArticle.setId(SecureUtil.md5(baseUri));
            htmlArticle.setUrl(baseUri);
            htmlArticle.setDomain(UrlUtils.getDomain(baseUri));
        }
        if (this.config.getTitleSelect().getSelectType() == SelectType.ARTICLE){
            initDefaultArticle();
            htmlArticle.setTitle(this.defaultArticle.getTitle());
        }
        else{
            htmlArticle.setTitle(ProcessorUtils.getSelectStrVal(this, this.config.getTitleSelect()));
        }
        if (this.config.getTimeSelect().getSelectType() == SelectType.ARTICLE){
            initDefaultArticle();
            htmlArticle.setTime(this.defaultArticle.getTime());
        }
        else{
            htmlArticle.setTime(ProcessorUtils.getSelectStrVal(this, this.config.getTimeSelect()));
        }

        if (this.config.getSummarySelect().stream().filter(f->f.getSelectType() == SelectType.ARTICLE).count() >= 1){
            initDefaultArticle();
            htmlArticle.setSummary(this.defaultArticle.getSummary());
        }
        else{
            htmlArticle.setSummary(ProcessorUtils.getSelectStrVal(this, this.config.getSummarySelect()));
        }

        if (this.config.getHtmlSelect().stream().filter(f->f.getSelectType() == SelectType.ARTICLE).count() >= 1){
            initDefaultArticle();
            htmlArticle.setHtml(this.defaultArticle.getHtml());
        }
        else{
            htmlArticle.setHtml(ProcessorUtils.getSelectStrVal(this, this.config.getHtmlSelect()));
        }

        if (this.config.getKeywordSelect().stream().filter(f->f.getSelectType() == SelectType.ARTICLE).count() >= 1){
            initDefaultArticle();
            htmlArticle.setKeyword(this.defaultArticle.getKeyword());
        }
        else{
            htmlArticle.setKeyword(ProcessorUtils.getSelectStrVal(this, this.config.getKeywordSelect()));
        }

        if (this.config.getImgSelect().stream().filter(f->f.getSelectType() == SelectType.ARTICLE).count() >= 1){
            initDefaultArticle();
            htmlArticle.setImages(this.defaultArticle.getImages());
        }
        else{
            htmlArticle.setImages(ProcessorUtils.getSelectStrVal(this, this.config.getImgSelect()));
        }
        htmlArticle.setContent(HtmlUtil.cleanHtmlTag(htmlArticle.getHtml()));
        return htmlArticle;
    }

}
