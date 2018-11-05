package com.jointsky.edps.spider.model;

import cn.hutool.db.Entity;

import java.sql.Timestamp;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-29.
 */
public class RequestInfo {
    private String id;
    private String url;
    private int priority;
    private int reqState;
    private Timestamp createDtm;
    private Timestamp doneDtm;
    private String request;

    public Timestamp getCreateDtm() {
        return createDtm;
    }

    public void setCreateDtm(Timestamp createDtm) {
        this.createDtm = createDtm;
    }

    public Timestamp getDoneDtm() {
        return doneDtm;
    }

    public void setDoneDtm(Timestamp doneDtm) {
        this.doneDtm = doneDtm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getReqState() {
        return reqState;
    }

    public void setReqState(int reqState) {
        this.reqState = reqState;
    }


    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Entity toEntity(String tbName){
        Entity entity = new Entity(tbName);
        entity.set("ID", this.id).set("URL", this.url)
                .set("PRIORITY", this.priority)
                .set("REQ_STA", this.reqState)
                .set("CREATE_DTM", this.createDtm)
                .set("DONE_DTM", this.doneDtm)
                .set("REQUEST", this.request);
        return entity;
    }

    public RequestInfo entityToRequestInfo(Entity entity){
        RequestInfo info = new RequestInfo();
        info.setId(entity.getStr("ID"));
        info.setUrl(entity.getStr("URL"));
        info.setPriority(entity.getInt("PRIORITY"));
        info.setReqState(entity.getInt("REQ_STA"));
        info.setCreateDtm(entity.getTimestamp("CREATE_DTM"));
        info.setDoneDtm(entity.getTimestamp("DONE_DTM"));
        info.setRequest(entity.getStr("REQUEST"));
        return info;
    }
}
