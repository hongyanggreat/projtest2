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
import com.gk.htc.sendMT.VNPT_ND.Send2VNPT_ND;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class VNPT_ND_Task extends _AbstractThreadSend2Provider {

    private static final Logger logger = Logger.getLogger(VNPT_ND_Task.class);
    private static final WorkQueue WORKQUEUE_VINA_ND = new WorkQueue("WORKQUEUE_VINA_ND", 100);
    private static final String QUEUE_NAME = "Q-->SEND_TO_VINA_ND";

    public VNPT_ND_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_VINA_ND.getMaxPoolSize();
        this.setName("VNPT_ND_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_VINA_ND);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_VINA_ND.execute(doSendBrand_runnable(oneQueueBr));
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
                    String[] tmp = Send2VNPT_ND.sendOneQueue(oneQueue);
                    long delay = working.done();
                    oneQueue.setProcessTime(delay);
                    if (tmp[0].equals(Send2VNPT_ND.STATUS.SUCCESS.val + "")) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                    } else if (tmp[0].equals(Send2VNPT_ND.STATUS.EXCEPTION.val + "")
                            || tmp[0].equals(Send2VNPT_ND.STATUS.PARSE_RESULT_ERROR.val + "")) {
                        // Qua TPS thi Log Lai de gui qua 3 lan thi log Submit thanh loi
                        int retry = oneQueue.getRetry();
                        if (retry < 3) {
                            oneQueue.setRetry();
                            oneQueue.setCacheFrom(" Send2_VNPT_ND_Task.UNKNOW_ERROR");
                            SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                            // Return luon de khong khi log CDR va Log Submit
                            return;
                        }
                    } else {
                        oneQueue.setResult(Tool.getInt(tmp[0]));
                        oneQueue.setErrorInfo(tmp[1]);
                    }

                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom(" Send2_VNPT_ND_Task:" + e.getMessage());
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
