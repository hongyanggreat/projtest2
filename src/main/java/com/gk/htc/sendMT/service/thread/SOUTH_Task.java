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
import com.gk.htc.sendMT.SOUTH.Send2SouthUnicode;
import com.gk.htc.sendMT.SOUTH.SouthResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class SOUTH_Task extends _AbstractThreadSend2Provider {

    private static final Logger logger = Logger.getLogger(SOUTH_Task.class);
    private static final WorkQueue WORKQUEUE_SOUTH = new WorkQueue("WORKQUEUE_SOUTH", 100);
    private static final String QUEUE_NAME = "Q-->SEND_TO_SOUTH";

    public SOUTH_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_SOUTH.getMaxPoolSize();
        this.setName("SOUTH_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_SOUTH);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_SOUTH.execute(doSendBrand_runnable(oneQueueBr));
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
//                    SouthResponse resp = Send2South.sendOneQueue(oneQueue);
                    SouthResponse resp = Send2SouthUnicode.sendOneQueue(oneQueue);
                    long delay = working.done();
                    String info = oneQueue.getLabel() + " -> [" + oneQueue.getSendTo() + "] d=" + delay;
                    Tool.debug(info);
                    oneQueue.setProcessTime(delay);
                    if ("1".equals(resp.getStatus())) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                    } else {
                        oneQueue.setResult(Tool.getInt(resp.getStatus()));
                        oneQueue.setErrorInfo(resp.getDescription());
                    }
                    //--
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom(" Send2_SOUTH_Task:" + e.getMessage());
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
