package com.jointsky.edps.spider.scheduler;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import com.jointsky.edps.spider.common.SysConstant;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.sql.SQLException;
import java.util.*;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-29.
 */
public class H2Scheduler extends DbScheduler {

    private static Db db = Db.use("group_def_spider");
    private static final int FETCH_SIZE = 500;
    private Set<String> tabSet = new HashSet<>();

    @Override
    public void resetDuplicateCheck(Task task) {
        createTable(getTbName(task), true);
    }

    private String getTbName(Task task){
        String URL_PREFIX = "LK_";
        return URL_PREFIX + task.getUUID().toUpperCase();
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        String tbName = getTbName(task);
        if (!tabSet.contains(tbName)){
            createTable(tbName, false);
            tabSet.add(tbName);
        }
        String id = SecureUtil.md5(request.getUrl());
        String sql = SysConstant.SPIDER_TABLE_URL_EXIST.replace("#TABLE#", tbName).replace("#ID#", id);
        try {
            return db.queryNumber(sql).intValue() > 0;
        } catch (SQLException e) {
            StaticLog.error(e);
        }
        return false;
    }

    public boolean bulkPush(Request[] requests, Task task){
        if (requests.length <= 0){
            return true;
        }
        List<Entity> entryList = new ArrayList<>();
        String tbName = getTbName(task);
        for (Request request : requests) {
            entryList.add(getEntity(tbName, request));
        }
        try {
            db.insert(entryList);
            return true;
        } catch (SQLException e) {
            StaticLog.error(e);
        }
        return false;
    }

    private Entity getEntity(String tbName, Request request){
        String id = SecureUtil.md5(request.getUrl());
        Entity entity = Entity.create(tbName);
        entity.set("ID", id).set("URL", request.getUrl())
                .set("PRIORITY", (int)request.getPriority())
                .set("REQ_STA", 0).set("UPDATE_DTM", new Date())
                .set("REQUEST", Base64Encoder.encode(JSON.toJSONBytes(request)));
        return entity;
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        String tbName = getTbName(task);
        try {
            String sql = SysConstant.SPIDER_TABLE_LEFT_NUM.replace("#TABLE#", tbName);
            return db.queryNumber(sql).intValue();
        } catch (SQLException e) {
            StaticLog.error(e);
        }
        return 0;
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        String tbName = getTbName(task);
        try {
            String sql = SysConstant.SPIDER_TABLE_TOTAL_NUM.replace("#TABLE#", tbName);
            return db.queryNumber(sql).intValue();
        } catch (SQLException e) {
            StaticLog.error(e);
        }
        return 0;
    }


    public List<Request> fetchRequest(Task task) {
        clearConsumedRequest(task);
        List<Request> requestList = new ArrayList<>();
        String tbName = getTbName(task);
        try {
            String sql = SysConstant.SPIDER_TABLE_QUERY_LEFT.replace("#TABLE#", tbName).replace("#SIZE#", String.valueOf(FETCH_SIZE));
            List<Entity> entityList = db.query(sql);
            if (entityList == null || entityList.size() <= 0) {
                if (isQueueHasValue()){
                    pushWhenNoDuplicate(null, task);
                    return fetchRequest(task);
                }
                return requestList;
            }
            for (Entity entity : entityList) {
                String reqBin = entity.getStr("REQUEST");
                requestList.add(JSON.parseObject(Base64Decoder.decodeStr(reqBin), Request.class));
            }
            return requestList;
        } catch (Exception e) {
            StaticLog.error(e);
        }
        return null;
    }

    public void clearConsumedRequest(Task task) {
        if (hasConsumed == null || hasConsumed.size() <= 0){
            return;
        }
        String tbName = getTbName(task);
        StringBuilder sqlBuilder = new StringBuilder();
        for (Request request : hasConsumed) {
            String id = SecureUtil.md5(request.getUrl());
            sqlBuilder.append(SysConstant.SPIDER_TABLE_UPDATE_URL.replace("#TABLE#", tbName).replace("#ID#", id));
        }
        try {
            db.execute(sqlBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(String tableName, boolean reCreate){
        String sql = (reCreate ? SysConstant.SPIDER_TABLE_RECREATE : SysConstant.SPIDER_TABLE_CREATE).replace("#TABLE#", tableName);
        try {
            db.execute(sql);
        } catch (SQLException e) {
            StaticLog.error(e);
        }
    }

}
