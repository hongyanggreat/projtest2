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
import com.gk.htc.sendMT.VasVTE.ViaHTC.ClientHtcVasVte;
import com.gk.htc.sendMT.VasVTE.ViaHTC.ResultHTC;

/**
 *
 * @author TUANPLA
 */
public class VasVTE_ViaHTC_Task extends _AbstractThreadSend2Provider {

    static Logger logger = Logger.getLogger(VasVTE_ViaHTC_Task.class);
    private static final WorkQueue WORKQUEUE_HTC__VAS_VT_BULKAPI = new WorkQueue("WORKQUEUE_HTC__VAS_VT_BULKAPI", 35);
    private static final String QUEUE_NAME = "Q-->SEND_TO_VASVTE_VIA_HTC";

    public VasVTE_ViaHTC_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_HTC__VAS_VT_BULKAPI.getMaxPoolSize();
        this.setName("VasVTE_ViaHTC_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_HTC__VAS_VT_BULKAPI);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_HTC__VAS_VT_BULKAPI.execute(doSendBrand_runnable(oneQueueBr));
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
                    ResultHTC resp = ClientHtcVasVte.wsCpMt(oneQueue);
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
                    oneQueue.setCacheFrom(" VasVTE_ViaHTC_Task:" + e.getMessage());
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
