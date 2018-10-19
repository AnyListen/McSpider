package com.jointsky.edps.spider.common;

import org.jsoup.nodes.Document;
import us.codecraft.webmagic.selector.Html;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-19.
 */
public class ExtendHtml extends Html {


    public ExtendHtml(String text, String url) {
        super(text, url);
    }

    public ExtendHtml(String text) {
        super(text);
    }

    public ExtendHtml(Document document) {
        super(document);
    }
}
