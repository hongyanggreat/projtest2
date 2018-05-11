/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.service.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.service.primarywork.Queue;

/**
 *
 * @author TUANPLA
 */
abstract public class _AbstractThreadSend2Provider extends Thread {

    public static final String SUCCESS_MSG = "SUBMITTED";
    public static final int SUCCESS_INT = 1;
    public static final int FAIL_SEND = 0;
    public static final int EXCEPTION = -1;
    protected boolean stop = false;
    protected int TPS = 10;
    Queue<SmsBrandQueue> queue;

    public _AbstractThreadSend2Provider(String queueName) {
        this.queue = new Queue(queueName);
    }

    public void addToqueue(SmsBrandQueue item) {
        queue.enqueue(item);
    }

    public void StoreQueue() {
        while (!queue.isEmpty()) {
            SmsBrandQueue one = queue.dequeue();
            one.setCacheFrom(this.getName() + ".StoreQueue()");
            SmsQueueDao.writeBrandQueue(one, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
        }
    }

    abstract protected Runnable doSendBrand_runnable(final SmsBrandQueue oneQueue);

    protected void logData(SmsBrandQueue brQueue) {
        AppStart.log_submitTask.addToqueue(brQueue);
        MyLog.logSubmit(SmsQueueDao.toStringJson(brQueue));
    }

    public void shutDown() {
        stop = Boolean.TRUE;
        SmsBrandQueue oneQueue = new SmsBrandQueue();
        oneQueue.setShutDown(stop);
        addToqueue(oneQueue);
    }

}
