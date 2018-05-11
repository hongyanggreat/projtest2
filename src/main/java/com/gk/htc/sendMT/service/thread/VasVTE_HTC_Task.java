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
import com.gk.htc.sendMT.VasVTE.HTC.ResultBO;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class VasVTE_HTC_Task extends _AbstractThreadSend2Provider {

    static Logger logger = Logger.getLogger(VasVTE_HTC_Task.class);
    //--
    private static final String USER = "bulk_htc";
    private static final String PASS = "123456a@";
    private static final String CP_CODE = "HTC";
    private static final String COMMAND_CODE = "bulksms";

    private static final String CONTENT_TYPE_ASCII = "0";
    private static final String CONTENT_TYPE_UNICODE = "1";

//    //---
    private static final WorkQueue WORKQUEUE_HTC__VAS_VT = new WorkQueue("WORKQUEUE_HTC__VAS_VT", 100);
    private static final String QUEUE_NAME = "Q-->SEND_TO_WS_VAS_VT";

    public VasVTE_HTC_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_HTC__VAS_VT.getMaxPoolSize();
//        TPS = pool.getMaximumPoolSize();
        this.setName("VasVTE_HTC_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_HTC__VAS_VT);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_HTC__VAS_VT.execute(doSendBrand_runnable(oneQueueBr));
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
                    ResultBO resp = dowsCpMt(oneQueue.getId() + "", oneQueue.getPhone(), oneQueue.getPhone(), oneQueue.getLabel(), COMMAND_CODE, oneQueue.getMessage(), CONTENT_TYPE_ASCII);
                    long delay = working.done();
                    oneQueue.setProcessTime(delay);

                    if (resp.getResult() == 1) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                        oneQueue.setProviderDesc(resp.getMessage());
                    } else {
                        oneQueue.setProviderDesc(resp.getMessage());
                        oneQueue.setResult(Integer.parseInt(resp.getResult() + ""));
                        oneQueue.setErrorInfo(resp.getMessage());
//                        oneQueue.setResult(0);   // Gan lai la Send Fail
                    }
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom(" Send2_VasVTE_Task:" + e.getMessage());
                    if (oneQueue.getRetry() > 3) {
                        logData(oneQueue);
                    } else {
                        SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                    }
                }
            }
        };
    }

    private static ResultBO dowsCpMt(java.lang.String requestID, java.lang.String userID, java.lang.String receiverID, java.lang.String serviceID, java.lang.String commandCode, java.lang.String content, java.lang.String contentType) {
        com.gk.htc.sendMT.VasVTE.HTC.WsBulkSms_Service service = new com.gk.htc.sendMT.VasVTE.HTC.WsBulkSms_Service();
        com.gk.htc.sendMT.VasVTE.HTC.WsBulkSms port = service.getWsBulkSmsPort();
        return port.wsCpMt(USER, PASS, CP_CODE, requestID, userID, receiverID, serviceID, commandCode, content, contentType);
    }

}
