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
import com.gk.htc.sendMT.HNK.HNKResponse;
import com.gk.htc.sendMT.HNK.Send2HNK;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class HNK_Task extends _AbstractThreadSend2Provider {

    private static final Logger logger = Logger.getLogger(HNK_Task.class);
    private static final WorkQueue WORKQUEUE_HNK = new WorkQueue("WORKQUEUE_HNK", 50);
    private static final String QUEUE_NAME = "Q-->SEND_TO_HNK";

    public HNK_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_HNK.getMaxPoolSize();
        this.setName("HNK_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_HNK);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_HNK.execute(doSendBrand_runnable(oneQueueBr));
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
                    HNKResponse resp = Send2HNK.sendOneQueue(oneQueue);
                    long delay = working.done();
                    oneQueue.setProcessTime(delay);
                    if ("1".equals(resp.getErrorid())) {                      
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                        oneQueue.setReqId_vivas(resp.getTransactionId());  // REQID
                    } else {
                        oneQueue.setResult(Tool.getInt(resp.getErrorid()));
                        oneQueue.setErrorInfo(resp.getErrordesc());
                        oneQueue.setReqId_vivas(resp.getTransactionId());  // REQID
                    }
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom(" Send2_HNK_Task:" + e.getMessage());
                    if (oneQueue.getRetry() > 3) {
                        logData(oneQueue);
                    } else {
                        SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                    }
                }
            }
        };

    }

}
