package com.jointsky.edps.spider.processor;

import com.jointsky.edps.spider.config.SiteConfig;
import com.jointsky.edps.spider.utils.ProcessorUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class JsonProcessor implements PageProcessor {

    private Site site;
    private SiteConfig siteConfig;

    public JsonProcessor(SiteConfig siteConfig) {
        this.siteConfig = siteConfig;
        this.site = ProcessorUtils.buildSite(this.siteConfig);
    }

    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return this.site;
    }
}
