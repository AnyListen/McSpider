//package com.jointsky.edps.spider.scheduler;
//
//import bloomfilter.CanGenerateHashFrom;
//import bloomfilter.mutable.BloomFilter;
//import us.codecraft.webmagic.Request;
//import us.codecraft.webmagic.Task;
//import us.codecraft.webmagic.scheduler.component.DuplicateRemover;
//
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * edps-spider
// * edps-spider
// * Created by hezl on 2018-10-29.
// */
//public class BloomFilterDuplicateRemover implements DuplicateRemover {
//
//    private AtomicInteger counter;
//    private BloomFilter<String> bloomFilter;
//    private int expectedElements;
//    private double falsePositiveRate;
//
//    public BloomFilterDuplicateRemover(){
//        this(10000000, 0.01);
//    }
//
//    public BloomFilterDuplicateRemover(int expectedElements){
//        this(expectedElements, 0.01);
//    }
//
//    public BloomFilterDuplicateRemover(int expectedElements, double falsePositiveRate) {
//        this.expectedElements = expectedElements;
//        this.falsePositiveRate = falsePositiveRate;
//        resetBloomFilter();
//    }
//
//    public void resetBloomFilter(){
//        this.counter = new AtomicInteger(0);
//        this.bloomFilter = BloomFilter.apply(this.expectedElements,
//                this.falsePositiveRate,
//                CanGenerateHashFrom.CanGenerateHashFromString$.MODULE$);
//    }
//
//    @Override
//    public boolean isDuplicate(Request request, Task task) {
//        boolean isDuplicate = bloomFilter.mightContain(getUrl(request));
//        if (!isDuplicate) {
//            bloomFilter.add(getUrl(request));
//            counter.incrementAndGet();
//        }
//        return isDuplicate;
//    }
//
//    public void addUrls(List<String> urls){
//        urls.forEach(url -> this.bloomFilter.add(url));
//    }
//
//    private String getUrl(Request request) {
//        return request.getUrl();
//    }
//
//    @Override
//    public void resetDuplicateCheck(Task task) {
//        resetBloomFilter();
//    }
//
//    @Override
//    public int getTotalRequestsCount(Task task) {
//        return counter.get();
//    }
//
//    public void close(){
//        if (this.bloomFilter != null){
//            this.bloomFilter.dispose();
//        }
//    }
//}
