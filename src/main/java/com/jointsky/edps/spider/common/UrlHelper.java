package com.jointsky.edps.spider.common;

import us.codecraft.webmagic.Page;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class UrlHelper {
    public static UrlType getUrlType(Page page){
        return (UrlType) page.getRequest().getExtra(SysConstant.URL_TYPE);
    }
}
