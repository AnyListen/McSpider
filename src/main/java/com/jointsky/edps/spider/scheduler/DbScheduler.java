package com.jointsky.edps.spider.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;
import us.codecraft.webmagic.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-11-05.
 */
public abstract class DbScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {
    private static final int INITIAL_CAPACITY = 50;
    private static final int BULK_SIZE = 50;
    private static final int FETCH_SIZE = 500;

    protected List<Request> toBeConsumed= new ArrayList<>(FETCH_SIZE);
    protected List<Request> hasConsumed= new ArrayList<>(FETCH_SIZE);

    private BlockingQueue<Request> noPriorityQueue = new LinkedBlockingQueue<>();

    private PriorityBlockingQueue<Request> priorityQueuePlus = new PriorityBlockingQueue<>(INITIAL_CAPACITY, (o1, o2) -> -NumberUtils.compareLong(o1.getPriority(), o2.getPriority()));

    private PriorityBlockingQueue<Request> priorityQueueMinus = new PriorityBlockingQueue<>(INITIAL_CAPACITY, (o1, o2) -> -NumberUtils.compareLong(o1.getPriority(), o2.getPriority()));

    @Override
    public abstract void resetDuplicateCheck(Task task);

    @Override
    public abstract boolean isDuplicate(Request request, Task task);

    @Override
    protected synchronized void pushWhenNoDuplicate(Request request, Task task) {
        int size = request == null ? 1 : BULK_SIZE;
        if (noPriorityQueue.size() >= size){
            clearQueue(noPriorityQueue, task);
        }
        if (priorityQueuePlus.size() >= size){
            clearQueue(noPriorityQueue, task);
        }
        if (priorityQueueMinus.size() >= size){
            clearQueue(noPriorityQueue, task);
        }
        if (request != null){
            if (isDuplicate(request, task)){
                return;
            }
            if (request.getPriority() == 0) {
                noPriorityQueue.add(request);
            } else if (request.getPriority() > 0) {
                priorityQueuePlus.put(request);
            } else {
                priorityQueueMinus.put(request);
            }
        }
    }

    private void clearQueue(Queue<Request> queue, Task task){
        Request[] requests = queue.toArray(new Request[0]);
        if (bulkPush(requests, task)){
            queue.clear();
        }
    }

    public abstract boolean bulkPush(Request[] requests, Task task);

    @Override
    public abstract int getLeftRequestsCount(Task task);

    @Override
    public abstract int getTotalRequestsCount(Task task);

    @Override
    public synchronized Request poll(Task task) {
        if (toBeConsumed == null || toBeConsumed.size() <= 0){
            toBeConsumed = fetchRequest(task);
        }
        if (toBeConsumed == null || toBeConsumed.size() <= 0){
            return null;
        }
        Request request = toBeConsumed.remove(0);
        if (request != null){
            hasConsumed.add(request);
        }
        return request;
    }

    public abstract List<Request> fetchRequest(Task task);

    public abstract void clearConsumedRequest(Task task);

    public boolean isQueueHasValue(){
        return noPriorityQueue.size() > 0 || priorityQueuePlus.size() > 0 || priorityQueueMinus.size() > 0;
    }
}
