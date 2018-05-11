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
import com.gk.htc.sendMT.VTEHNi.AD.ClientVTEHNi;
import com.gk.htc.sendMT.VTEHNi.AD.ResultAD;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class VTEHni_ViaAD_Task extends _AbstractThreadSend2Provider {

    static Logger logger = Logger.getLogger(VTEHni_ViaAD_Task.class);
    private static final WorkQueue WORKQUEUE_AD__VTEHni = new WorkQueue("WORKQUEUE_AD__VTEHni", 35);
    private static final String QUEUE_NAME = "Q-->SEND_TO_VTEHNI_VIA_AD";

    public VTEHni_ViaAD_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_AD__VTEHni.getMaxPoolSize();
        this.setName("VTEHni_ViaAD_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_AD__VTEHni);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_AD__VTEHni.execute(doSendBrand_runnable(oneQueueBr));
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
                    ResultAD resp = ClientVTEHNi.wsCpMt(oneQueue);
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
                        oneQueue.setResult(0);   // Gan lai la Send Fail
                    }
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom(" VTEHni_ViaAD_Task:" + e.getMessage());
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
