package com.jointsky.edps.spider.processor;

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

    public JsonProcessor(Site site){
        this.site = site;
    }

    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return this.site;
    }
}
