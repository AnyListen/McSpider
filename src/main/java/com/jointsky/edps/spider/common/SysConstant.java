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


    public static final String ID = "ID";
    public static final String URL = "URL";
    public static final String DOMAIN = "DOMAIN";
    public static final String TITLE = "TITLE";
    public static final String SUMMARY = "SUMMARY";
    public static final String KEYWORD = "KEYWORD";
    public static final String HTML = "HTML";
    public static final String CONTENT = "CONTENT";
    public static final String IMAGE = "IMAGE";
    public static final String TIME = "TIME";

    public static final String SPIDER_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS #TABLE# (ID VARCHAR(36) PRIMARY KEY, URL VARCHAR(1000) NOT NULL, PRIORITY TINYINT DEFAULT 0, REQ_STA TINYINT DEFAULT 0, UPDATE_DTM TIMESTAMP NOT NULL, REQUEST CLOB);\n" +
            "CREATE INDEX IF NOT EXISTS #TABLE#__PRIORITY ON #TABLE# (PRIORITY DESC);\n" +
            "CREATE INDEX IF NOT EXISTS #TABLE#__REQ_STA ON #TABLE# (REQ_STA);\n" +
            "CREATE INDEX IF NOT EXISTS #TABLE#__CRT_DTM ON #TABLE# (UPDATE_DTM);";

    public static final String SPIDER_TABLE_RECREATE = "DROP TABLE IF EXISTS #TABLE#;CREATE TABLE #TABLE# (ID VARCHAR(36) PRIMARY KEY, URL VARCHAR(500) NOT NULL, PRIORITY TINYINT DEFAULT 0, REQ_STA TINYINT DEFAULT 0, UPDATE_DTM TIMESTAMP NOT NULL, REQUEST CLOB);\n" +
            "CREATE INDEX IF NOT EXISTS #TABLE#__PRIORITY ON #TABLE# (PRIORITY DESC);\n" +
            "CREATE INDEX IF NOT EXISTS #TABLE#__REQ_STA ON #TABLE# (REQ_STA);\n" +
            "CREATE INDEX IF NOT EXISTS #TABLE#__CRT_DTM ON #TABLE# (UPDATE_DTM);";

    public static final String SPIDER_TABLE_EXITS = "SELECT COUNT(1) FROM INFORMATION_SCHEMA.TABLES t where t.TABLE_NAME = '#TABLE#';";

    public static final String SPIDER_TABLE_TOTAL_NUM = "SELECT COUNT(1) FROM #TABLE#;";
    public static final String SPIDER_TABLE_LEFT_NUM = "SELECT COUNT(1) FROM #TABLE# t where t.REQ_STA = 0;";

    public static final String SPIDER_TABLE_QUERY_LEFT = "SELECT * FROM #TABLE# t where t.REQ_STA = 0 ORDER BY t.PRIORITY DESC, t.UPDATE_DTM ASC LIMIT #SIZE#;";

    public static final String SPIDER_TABLE_URL_EXIST = "SELECT COUNT(1) FROM #TABLE# t where t.ID = '#ID#';";
    public static final String SPIDER_TABLE_UPDATE_URL = "UPDATE #TABLE# SET REQ_STA = 1,UPDATE_DTM=CURRENT_TIMESTAMP() WHERE ID='#ID#';";
}
