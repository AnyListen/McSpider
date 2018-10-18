package com.jointsky.edps.spider.utils;

import com.jointsky.edps.spider.common.SelectType;
import com.jointsky.edps.spider.config.FieldSelectConfig;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-18.
 */
public class ProcessorUtils {

    public static Selectable getSelectVal(Html pageHtml, FieldSelectConfig fConfig){
        return getSelectVal(pageHtml, fConfig.getConfigText(), fConfig.getSelectType(), fConfig.getGroup());
    }

    public static Selectable getSelectVal(Html pageHtml, String selConfig, SelectType selectType){
        return getSelectVal(pageHtml, selConfig, selectType, 0);
    }

    public static Selectable getSelectVal(Html pageHtml, String selConfig, SelectType selectType, int groupNum){
        Selectable value;
        switch (selectType){
            case CSS:
                value = pageHtml.$(selConfig);
                break;
            case REGEX:
                value = pageHtml.regex(selConfig, groupNum);
                break;
            case XPATH:
                value = pageHtml.xpath(selConfig);
                break;
            case JPATH:
                value = pageHtml.jsonPath(selConfig);
                break;
            default:
                value = new PlainText(selConfig);
                break;
        }
        return value;
    }
}
