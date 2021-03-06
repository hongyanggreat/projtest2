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

/**
 *
 * @author TUANPLA
 */
public class ANTIT_Task extends _AbstractThreadSend2Provider {

    private static final Logger logger = Logger.getLogger(ANTIT_Task.class);
    private static final String USER = "htc";
    private static final String PASS = "htc#!@";

    private static final WorkQueue WORKQUEUE_ANTIT = new WorkQueue("WORKQUEUE_ANTIT", 10);
    private static final String QUEUE_NAME = "Q-->SEND_TO_ANTIT_QUEUE";

    /**
     *
     */
    public ANTIT_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_ANTIT.getMaxPoolSize();
        this.setName("ANTIT_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_ANTIT);
    }

    @Override
    public void run() {
        try {
            Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
            while (AppStart.isRuning & !stop) {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_ANTIT.execute(doSendBrand_runnable(oneQueueBr));
                }
                Thread.sleep(1000 / TPS);
            }
            StoreQueue();
            MonitorWorker.removeDemonName(this.getName());
        } catch (Exception e) {
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
                    String repResult = doSendMT(oneQueue.getPhone(), oneQueue.getLabel(), oneQueue.getMessage());
                    // Gui Xong
                    long delay = working.done();
                    oneQueue.setProcessTime(delay);
                    if (repResult.startsWith(STATUS.SUCCESS.val + "")) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                    } else {
                        oneQueue.setResult(Tool.getInt(repResult));
                        oneQueue.setErrorInfo(STATUS.getmessage(Tool.getInt(repResult)));
                    }
                    //--logData
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom("Send2ANTIT:" + e.getMessage());
                    if (oneQueue.getRetry() > 3) {
                        logData(oneQueue);
                    } else {
                        SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                    }
                }
            }
        };
    }

    public static enum STATUS {

        SUCCESS(200, "Success"), // Thuc chat la VMG
        WRONG_LABEL(301, "WRONG BRAND NAME"),
        MAX_SMS_LENGTH(302, "MAXIUM MESSAGE LENGTH"),
        NOT_ENOUGH(303, "NOT ENOUGH"),
        LABEL_NOT_ROUTE(304, "BRANDNAME IS NOT ROUTE"),
        WRONG_MSISDN(305, "WRONG MSISDN"),
        ACCOUNT_LOCK(402, "ACCOUNT IS NOT ACTIVE"),
        WRONG_ACCOUNT(403, "WRONG ACCOUNT"),
        FAIL(405, "FAIL"),
        DUPLICATE_SMS(406, "DUPLICATE SMS"), //-
        ;
        public int val;
        public String mess;

        private STATUS(int val, String mess) {
            this.val = val;
            this.mess = mess;
        }

        public static String getmessage(int val) {
            String str = "Unknow Result ANTIT VAL:" + val;
            for (STATUS one : STATUS.values()) {
                if (one.val == val) {
                    str = one.mess;
                    break;
                }
            }
            return str;
        }
    }

    private static String doSendMT(String msisdn, String brandname, String msgbody) {
        return sendMT(msisdn, brandname, msgbody, USER, PASS);
    }

    private static String sendMT(String msisdn, String brandname, String msgbody, String user, String pass) {
        com.gk.htc.sendMT.ANTIT.AntitApi_Service service = new com.gk.htc.sendMT.ANTIT.AntitApi_Service();
        com.gk.htc.sendMT.ANTIT.AntitApi port = service.getAntitApiPort();
        return port.sendMT(msisdn, brandname, msgbody, user, pass);
    }
}
