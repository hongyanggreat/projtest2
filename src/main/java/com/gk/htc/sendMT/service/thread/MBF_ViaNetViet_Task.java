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
import com.gk.htc.sendMT.MBF.NETVIET.MBFSender;
import com.gk.htc.sendMT.MBF.NETVIET.Response;
import org.apache.log4j.Logger;

/**
 *
 * @author Private
 */
public class MBF_ViaNetViet_Task extends _AbstractThreadSend2Provider {

    private static final Logger logger = Logger.getLogger(MBF_ViaNetViet_Task.class);

    private static final WorkQueue WORKQUEUE_NV_MBF = new WorkQueue("WORKQUEUE_NV_MBF", 10);
    private static final String QUEUE_NAME = "Q-->QUEUE_SEND_MBF_VIA_NV";

    public MBF_ViaNetViet_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_NV_MBF.getMaxPoolSize();
        this.setName("MBF_Via_NetViet_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_NV_MBF);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_NV_MBF.execute(doSendBrand_runnable(oneQueueBr));
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
    protected Runnable doSendBrand_runnable(SmsBrandQueue oneQueue) {
        return new Runnable() {

            @Override
            public void run() {
                DoWork working = new DoWork();
                oneQueue.setTimeSend(DateProc.createTimestamp());
                try {
                    // Submit
                    Response resp = MBFSender.doSend_ASCII(oneQueue);
                    long delay = working.done();
                    oneQueue.setProcessTime(delay);
                    //--
                    if ("200".equals(resp.getStatus())) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                        oneQueue.setProviderDesc(resp.getMessage());
                    } else {
                        oneQueue.setProviderDesc(resp.getMessage());
                        oneQueue.setResult(Tool.getInt(resp.getStatus()));
                        oneQueue.setErrorInfo(resp.getMessage());
//                        oneQueue.setResult(0);   // Gan lai la Send Fail
                    }
                    //--
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom(" Send2MBF_Task:" + e.getMessage());
                    if (oneQueue.getRetry() >3) {
                        //--
                        logData(oneQueue);
                    } else {
                        SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                    }
                }
            }
        };
    }
}
