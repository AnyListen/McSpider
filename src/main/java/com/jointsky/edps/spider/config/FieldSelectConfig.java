package com.jointsky.edps.spider.config;

import com.jointsky.edps.spider.common.SelectType;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-17.
 */
public class FieldSelectConfig {
    private String filedName;
    private String configText;
    private SelectType selectType;
    private int group = 0;

    public FieldSelectConfig(){}

    public FieldSelectConfig(String filedName, String configText, SelectType selectType) {
        this.filedName = filedName;
        this.configText = configText;
        this.selectType = selectType;
    }

    public FieldSelectConfig(String filedName, String configText, SelectType selectType, int group) {
        this.filedName = filedName;
        this.configText = configText;
        this.selectType = selectType;
        this.group = group;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof FieldSelectConfig)){
//            return super.equals(obj);
//        }
//        FieldSelectConfig config = (FieldSelectConfig)obj;
//        if (config.getFiledName().equals(this.getFiledName()))
//
//    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getConfigText() {
        return configText;
    }

    public void setConfigText(String configText) {
        this.configText = configText;
    }

    public SelectType getSelectType() {
        return selectType;
    }

    public void setSelectType(SelectType selectType) {
        this.selectType = selectType;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }
}
