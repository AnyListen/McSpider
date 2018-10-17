package com.jointsky.edps.spider.common;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public enum SelectType {
    /**
     * 页面信息选择器
     */
    ARTICLE,
    /**
     * NLP 选择器
     */
    NLP,
    /**
     * 正则选择器
     */
    REGEX,
    /**
     * CSS 样式选择器
     */
    CSS,
    /**
     * XPATH 选择器
     */
    XPATH,
    /**
     * Json Path 选择器
     */
    JPATH,
    /**
     * 字段选择器
     */
    FIELD,
    /**
     * 静态字符串
     */
    NONE,
    /**
     * 自定义
     */
    CUSTOM;
}
