/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.service.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.DoWork;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.ahp.brand.service.primarywork.WorkQueue;
import org.apache.log4j.Logger;
import com.gk.htc.sendMT.VasVTE.MFS.ResultMBFS;
import com.gk.htc.sendMT.VasVTE.MFS.ClientMFSVasVTE;

/**
 *
 * @author TUANPLA
 */
public class VasVTE_ViaMFS_Task extends _AbstractThreadSend2Provider {

    static Logger logger = Logger.getLogger(VasVTE_ViaMFS_Task.class);
//    // TEST
//    RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
//    ThreadPoolExecutor pool = new ThreadPoolExecutor(0, // Core Pool
//            100, // Max Pool
//            5L, // keepAlived Time
//            TimeUnit.SECONDS, // Time Unit
//            new SynchronousQueue<>(), // Thread Queue   ==> Cached Thread Pool
//            handler);   // Error Hander
//    //---
    private static final WorkQueue WORKQUEUE_MBFS__VAS_VT = new WorkQueue("WORKQUEUE_MBFS__VAS_VT", 100);
    private static final String QUEUE_NAME = "Q-->SEND_TO_MBFS_VAS_VT";

    public VasVTE_ViaMFS_Task() {
        super(QUEUE_NAME);
//        TPS = pool.getMaximumPoolSize();
        TPS = WORKQUEUE_MBFS__VAS_VT.getMaxPoolSize();
        this.setName("VasVTE_MFS_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_MBFS__VAS_VT);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
//        DoWork work = new DoWork();
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
//                    try {
//                        pool.execute(doSendBrand_runnable(oneQueueBr));
//                    } catch (RejectedExecutionException e) {
//                        logger.error(Tool.getLogMessage(e));
//                        addToqueue(oneQueueBr);
//                    }
                    WORKQUEUE_MBFS__VAS_VT.execute(doSendBrand_runnable(oneQueueBr));
                }
                Thread.sleep(1000 / TPS);
            } catch (Exception e) {
                logger.error(Tool.getLogMessage(e));
            }
        }
        StoreQueue();
        MonitorWorker.removeDemonName(this.getName());
    }

    @Override
    protected Runnable doSendBrand_runnable(final SmsBrandQueue oneQueue) {
        return new Runnable() {
            @Override
            public void run() {
                DoWork working = new DoWork();
                oneQueue.setTimeSend(DateProc.createTimestamp());
                try {
                    // Submit
                    ResultMBFS resp = ClientMFSVasVTE.wsCpMt(oneQueue);
                    long delay = working.done();
                    oneQueue.setProcessTime(delay);
                    if ("1".equals(resp.getCode())) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                        oneQueue.setProviderDesc(resp.getMessage());
                    } else {
                        oneQueue.setProviderDesc(resp.getMessage());
                        oneQueue.setResult(Tool.getInt(resp.getCode()));
                        oneQueue.setErrorInfo(resp.getMessage());
                        oneQueue.setResult(Integer.parseInt(resp.getCode()));   // Gan lai la Send Fail
                    }
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo("--catch : "+ e.getMessage());
                    oneQueue.setCacheFrom(" VasVTE_MFS_Task:" + e.getMessage());
                    if (oneQueue.getRetry() > 3) {
                        logData(oneQueue);
                    } else {
                        SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                    }
                }
            }
        };
    }

//    private static void printStatus(ThreadPoolExecutor e) {
//
//        StringBuilder s = new StringBuilder();
//        s.append("WORK_MBFS__VAS_VT: Size=").append(e.getPoolSize())
//                .append(",core=").append(e.getCorePoolSize())
//                .append(",queueSize = ").append(e.getQueue().size())
//                .append(",queueRemainingCapacity = ").append(e.getQueue().remainingCapacity())
//                .append(",maximumPoolSize = ").append(e.getMaximumPoolSize())
//                .append(",getTaskCount = ").append(e.getTaskCount())
//                .append(",getCompletedTaskCount = ").append(e.getCompletedTaskCount());
////        System.out.println(s.toString());
//        MyLog.debug(s.toString(), Level.ALL);
//    }
}
