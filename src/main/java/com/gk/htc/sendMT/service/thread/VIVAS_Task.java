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
import com.gk.htc.sendMT.VIVAS.SendSMSVivas;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.ahp.brand.service.primarywork.WorkQueue;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class VIVAS_Task extends _AbstractThreadSend2Provider {

    static Logger logger = Logger.getLogger(VIVAS_Task.class);
    private static final WorkQueue WORKQUEUE_VIVAS = new WorkQueue("WORKQUEUE_VIVAS", 15);
    private static final String QUEUE_NAME = "Q-->SEND_TO_VIVAS";

    public VIVAS_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_VIVAS.getMaxPoolSize();
        this.setName("VIVAS_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_VIVAS);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_VIVAS.execute(doSendBrand_runnable(oneQueueBr));
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
                    String[] tmp = SendSMSVivas.sendOneQueue(oneQueue);
                    long delay = working.done();
                    oneQueue.setProcessTime(delay);
                    if (tmp[0].equals(SendSMSVivas.STATUS.SUCCESS.val + "")) {
                        // STATUS
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                        oneQueue.setReqId_vivas(tmp[1]);  // REQID
                    } else if (tmp[0].equals(SendSMSVivas.STATUS.OVER_TPS.val + "")) {
                        // Qua TPS thi Log Lai de gui qua 3 lan thi log Submit thanh loi
                        if (oneQueue.getRetry() < 3) {
                            oneQueue.setRetry();
                            oneQueue.setCacheFrom(" Send2Vivas.OVER_TPS()");
                            SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                            // Return luon de khong khi log CDR va Log Submit
                            return;
                        }
                    } else {
                        oneQueue.setResult(Tool.getInt(tmp[0]));
                        oneQueue.setErrorInfo(SendSMSVivas.STATUS.getmessage(Tool.getInt(tmp[0])));
                    }
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom(" Send2_Vivas_Task:" + e.getMessage());
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
