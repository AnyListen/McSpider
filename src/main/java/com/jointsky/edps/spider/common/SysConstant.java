package com.jointsky.edps.spider.common;

import java.util.HashMap;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class SysConstant {
    public static final String PAGE_SETTING = "PAGE_SETTING";
    public static final String SINGLE_ITEM = "SINGLE_ITEM";
    public static final String NULL_FILTER = "NULL_FILTER";
    public static final String EMPTY_FILTER = "EMPTY_FILTER";
    public static final String MIN_FILTER = "MIN_FILTER";
    public static final String MAX_FILTER = "MAX_FILTER";
    public static final String TYPE_FILTER = "TYPE_FILTER";

    public static final Map<String, Class> TYPE_MAP = new HashMap<String, Class>(){{
        put("STRING", String.class);
        put("NUMBER", Number.class);
    }};

    public static final String SIMPLE_FILTER_METHOD = "SIMPLE_FILTER_METHOD";
    public static final String SIMPLE_FILTER_TYPE = "SIMPLE_FILTER_TYPE";


    public static final String TITLE = "TITLE";
    public static final String SUMMARY = "SUMMARY";
    public static final String KEYWORD = "KEYWORD";
    public static final String HTML = "HTML";
    public static final String CONTENT = "CONTENT";
    public static final String IMAGE = "IMAGE";
    public static final String TIME = "TIME";
}
