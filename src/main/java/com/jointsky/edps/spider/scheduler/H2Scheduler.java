//package com.jointsky.edps.spider.scheduler;
//
//import com.jointsky.edps.spider.utils.H2Helper;
//import us.codecraft.webmagic.Request;
//import us.codecraft.webmagic.Task;
//import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
//import us.codecraft.webmagic.scheduler.MonitorableScheduler;
//import us.codecraft.webmagic.scheduler.Scheduler;
//
//import java.io.FileWriter;
//import java.io.PrintWriter;
//import java.util.LinkedHashSet;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * edps-spider
// * edps-spider
// * Created by hezl on 2018-10-29.
// */
//public class H2Scheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, Scheduler {
//
//    private AtomicBoolean inited = new AtomicBoolean(false);
//
//    private BlockingQueue<Request> queue;
//
//    private ScheduledExecutorService flushThreadPool;
//
//    public H2Scheduler() {
//        initDuplicateRemover();
//    }
//
//    private void initDuplicateRemover() {
//        setDuplicateRemover(new BloomFilterDuplicateRemover(10000000){
//            @Override
//            public boolean isDuplicate(Request request, Task task) {
//                if (!inited.get()) {
//                    initTask(task);
//                }
//                return super.isDuplicate(request, task);
//            }
//        });
//    }
//
//    private void initTask(Task task) {
//        String tbName = task.getUUID();
//        H2Helper.getTotalNum()
//
//
//
//    }
//
//    private void initFlushThread() {
//        flushThreadPool = Executors.newScheduledThreadPool(1);
//        flushThreadPool.scheduleAtFixedRate(this::flush, 10, 10, TimeUnit.SECONDS);
//    }
//
//    private void initWriter() {
//        try {
//            fileUrlWriter = new PrintWriter(new FileWriter(getFileName(fileUrlAllName), true));
//            fileCursorWriter = new PrintWriter(new FileWriter(getFileName(fileCursor), false));
//        } catch (IOException e) {
//            throw new RuntimeException("init cache scheduler error", e);
//        }
//    }
//
//    private void readFile() {
//        try {
//            queue = new LinkedBlockingQueue<Request>();
//            urls = new LinkedHashSet<String>();
//            readCursorFile();
//            readUrlFile();
//            // initDuplicateRemover();
//        } catch (FileNotFoundException e) {
//            //init
//            logger.info("init cache file " + getFileName(fileUrlAllName));
//        } catch (IOException e) {
//            logger.error("init file error", e);
//        }
//    }
//
//    private void readUrlFile() throws IOException {
//        String line;
//        BufferedReader fileUrlReader = null;
//        try {
//            fileUrlReader = new BufferedReader(new FileReader(getFileName(fileUrlAllName)));
//            int lineReaded = 0;
//            while ((line = fileUrlReader.readLine()) != null) {
//                urls.add(line.trim());
//                lineReaded++;
//                if (lineReaded > cursor.get()) {
//                    queue.add(new Request(line));
//                }
//            }
//        } finally {
//            if (fileUrlReader != null) {
//                IOUtils.closeQuietly(fileUrlReader);
//            }
//        }
//    }
//
//    private void readCursorFile() throws IOException {
//        BufferedReader fileCursorReader = null;
//        try {
//            fileCursorReader = new BufferedReader(new FileReader(getFileName(fileCursor)));
//            String line;
//            //read the last number
//            while ((line = fileCursorReader.readLine()) != null) {
//                cursor = new AtomicInteger(NumberUtils.toInt(line));
//            }
//        } finally {
//            if (fileCursorReader != null) {
//                IOUtils.closeQuietly(fileCursorReader);
//            }
//        }
//    }
//
//    public void close() throws IOException {
//        flushThreadPool.shutdown();
//        fileUrlWriter.close();
//        fileCursorWriter.close();
//    }
//
//    private String getFileName(String filename) {
//        return filePath + task.getUUID() + filename;
//    }
//
//    @Override
//    protected void pushWhenNoDuplicate(Request request, Task task) {
//        if (!inited.get()) {
//            init(task);
//        }
//        queue.add(request);
//        fileUrlWriter.println(request.getUrl());
//    }
//
//    @Override
//    public synchronized Request poll(Task task) {
//        if (!inited.get()) {
//            init(task);
//        }
//        fileCursorWriter.println(cursor.incrementAndGet());
//        return queue.poll();
//    }
//
//    @Override
//    public int getLeftRequestsCount(Task task) {
//        return queue.size();
//    }
//
//    @Override
//    public int getTotalRequestsCount(Task task) {
//        return getDuplicateRemover().getTotalRequestsCount(task);
//    }
//}
