/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.service.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.DoWork;
import com.gk.htc.ahp.brand.common.HtmlTool;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.ahp.brand.service.primarywork.WorkQueue;
import com.gk.htc.sendMT.MFS.ClientMOBI_Service;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class MOBI_Service_Task extends _AbstractThreadSend2Provider {

    private static final Logger logger = Logger.getLogger(MOBI_Service_Task.class);

    private static final WorkQueue WORKQUEUE_MOBI_SERVICE = new WorkQueue("WORKQUEUE_MOBI_SERVICE", 15);
    private static final String QUEUE_NAME = "Q-->QUEUE_SEND_MOBI_SERVICE";

    public MOBI_Service_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_MOBI_SERVICE.getMaxPoolSize();
        this.setName("MOBI_Service_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_MOBI_SERVICE);
    }

    @Override
    public void run() {
        try {
            Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
            while (AppStart.isRuning && !stop) {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_MOBI_SERVICE.execute(doSendBrand_runnable(oneQueueBr));
                }
                Thread.sleep(1000 / TPS);
            }
            StoreQueue();
            MonitorWorker.removeDemonName(this.getName());
        } catch (InterruptedException e) {
            logger.error(Tool.getLogMessage(e));
        }
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
//                    Tool.debug("VAO DEND DAY:" + info);
                    String repResult = ClientMOBI_Service.sendSMS(oneQueue.getSystemId(), oneQueue.getLabel(), oneQueue.getPhone(), oneQueue.getMessage());
                    long delay = working.done();
                    oneQueue.setProcessTime(delay);
                    if (repResult.contains("<ERRCODE>0</ERRCODE>")) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                        oneQueue.setProviderDesc(repResult);
                    } else {
                        oneQueue.setProviderDesc(repResult);
                        oneQueue.setResult(FAIL_SEND);
                        oneQueue.setErrorInfo(HtmlTool.html2text(repResult));
                    }
                    //--logData
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom(" Send2_MOBI_Service_Task:" + e.getMessage());
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
