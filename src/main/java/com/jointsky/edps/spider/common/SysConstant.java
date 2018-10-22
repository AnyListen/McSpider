package com.jointsky.edps.spider.common;

import java.util.HashMap;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class SysConstant {
    public static final String START_URL = "START_URL";
    public static final String URL_ID = "URL_ID";
    public static final String SINGLE_ITEM = "SINGLE_ITEM";
    public static final String NULL_FILTER = "";
    public static final String EMPTY_FILTER = "";
    public static final String MIN_FILTER = "";
    public static final String MAX_FILTER = "";
    public static final String TYPE_FILTER = "";

    public static final Map<String, Class> TYPE_MAP = new HashMap<String, Class>(){{
        put("STRING", String.class);
        put("NUMBER", Number.class);
    }};

    public static final String SIMPLE_FILTER_METHOD = "SIMPLE_FILTER_METHOD";
    public static final String SIMPLE_FILTER_TYPE = "SIMPLE_FILTER_TYPE";
}
