//package com.jointsky.edps.spider.utils;
//
//import cn.hutool.db.Db;
//import cn.hutool.db.Entity;
//import cn.hutool.log.StaticLog;
//import com.jointsky.edps.spider.common.SysConstant;
//import com.jointsky.edps.spider.model.RequestInfo;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * edps-spider
// * edps-spider
// * Created by hezl on 2018-10-26.
// */
//public class H2Helper {
//    private static Db db = Db.use("group_def_spider");
//
//    public static Db getDb(){
//        return db;
//    }
//
//    public static boolean createTable(String tableName, boolean reCreate){
//        tableName = tableName.toUpperCase();
//        String sql = (reCreate ? SysConstant.SPIDER_TABLE_RECREATE : SysConstant.SPIDER_TABLE_CREATE).replace("#TABLE#", tableName);
//        try {
//            db.execute(sql);
//            return true;
//        } catch (SQLException e) {
//            StaticLog.error(e.getMessage());
//            return false;
//        }
//    }
//
//    public static boolean isTableExits(String tableName) throws SQLException {
//        tableName = tableName.toUpperCase();
//        String sql = SysConstant.SPIDER_TABLE_EXITS.replace("#TABLE#", tableName);
//        return db.queryNumber(sql).intValue() == 1;
//    }
//
//    public static boolean insert(String tbName, RequestInfo requestInfo) throws SQLException {
//        tbName = tbName.toUpperCase();
//        return db.insert(requestInfo.toEntity(tbName)) == 1;
//    }
//
//    public static int[] insert(String tbName, List<RequestInfo> infoList) throws SQLException {
//        tbName = tbName.toUpperCase();
//        List<Entity> entityList = new ArrayList<>();
//        for (RequestInfo info : infoList) {
//            entityList.add(info.toEntity(tbName));
//        }
//        return db.insert(entityList);
//    }
//
//    public static int getTotalNum(String tbName) throws SQLException {
//        tbName = tbName.toUpperCase();
//        String sql = SysConstant.SPIDER_TABLE_TOTAL_NUM.replace("#TABLE#", tbName);
//        return db.queryNumber(sql).intValue();
//    }
//
//    public static List<String>
//
//    public static int getLeftNum(String tbName) throws SQLException {
//        tbName = tbName.toUpperCase();
//        String sql = SysConstant.SPIDER_TABLE_LEFT_NUM.replace("#TABLE#", tbName);
//        return db.queryNumber(sql).intValue();
//    }
//
////    public static List<RequestInfo> getLeftRequsets(String tbName, int size){
////
////    }
//}
