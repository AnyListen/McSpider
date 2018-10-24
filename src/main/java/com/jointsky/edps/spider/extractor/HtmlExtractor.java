package com.jointsky.edps.spider.extractor;

import com.jointsky.edps.spider.common.SelectType;
import com.jointsky.edps.spider.common.SysConstant;
import com.jointsky.edps.spider.config.FieldSelectConfig;
import com.jointsky.edps.spider.utils.ProcessorUtils;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-24.
 */
public class HtmlExtractor {

    private Html html;
    private ExtractorConfig config = initExtractor(null);

    public HtmlExtractor(Html html){
        this.html = html;
    }

    public HtmlExtractor(String html){
        this.html = Html.create(html);
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

    public HtmlExtractor setConfig(ExtractorConfig config) {
        this.config = config;
        return this;
    }

    public HtmlArticle getArticle(){
        HtmlArticle htmlArticle = new HtmlArticle();
        this.config = initExtractor(this.config);
        htmlArticle.setTitle(ProcessorUtils.getSelectStrVal(this.html, this.config.getTitleSelect()));
        htmlArticle.setTime(ProcessorUtils.getSelectStrVal(this.html, this.config.getTimeSelect()));
        htmlArticle.setSummary(ProcessorUtils.getSelectStrVal(this.html, this.config.getSummarySelect()));



        htmlArticle.setKeyword(ProcessorUtils.getSelectStrVal(this.html, this.config.getKeywordSelect()));
        htmlArticle.setTitle(ProcessorUtils.getSelectStrVal(this.html, this.config.getTitleSelect()));
        htmlArticle.setTitle(ProcessorUtils.getSelectStrVal(this.html, this.config.getTitleSelect()));

        return htmlArticle;
    }
}
