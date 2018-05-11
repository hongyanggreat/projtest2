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
public class CMC_Task extends _AbstractThreadSend2Provider {

    private static final Logger logger = Logger.getLogger(CMC_Task.class);
    private static final String USER = "htc";
    private static final String PASS = "htc@123!";

    private static final WorkQueue WORKQUEUE_CMC = new WorkQueue("WORKQUEUE_CMC", 5);
    private static final String QUEUE_NAME = "Q-->QUEUE_SEND_TO_CMC";

    public CMC_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_CMC.getMaxPoolSize();
        this.setName("CMC_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_CMC);
    }

    @Override
    public void run() {
        try {
            Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
            while (AppStart.isRuning & !stop) {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_CMC.execute(doSendBrand_runnable(oneQueueBr));
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
                    oneQueue.setCacheFrom("Send2CMC:" + e.getMessage());
                    if (oneQueue.getRetry() > 3) {
                        //=> TODO can nhan co ghi log CDR de Canh bao loi ko
                        logData(oneQueue);
                    } else {
                        SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                    }
                }
            }
        };
    }

    public static enum STATUS {

        SUCCESS(1, "Success"), // Thuc chat la VMG
        LOGIN_FAIL(-2, "Username/Password Invalid"),
        INVALID_PHONE(-3, "Invalid Phonenumber"),
        INVALID_MSG_LENGTH(-4, "Exceeds the message’s length"),
        TELCO_NOT_ALLOW(-7, "Invalid Telco"),
        SPAM(-8, "Spam"),
        INVALID_BRAND(-9, "Invalid Brandname"),
        ERROR(-1, "Unknow Error"),//
        ;
        public int val;
        public String mess;

        private STATUS(int val, String mess) {
            this.val = val;
            this.mess = mess;
        }

        public static String getmessage(int val) {
            String str = "Unknow Result CMC VAL:" + val;
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
        return sendSMSBrandName(msisdn, msgbody, brandname, USER, PASS);
    }

    private static String sendSMSBrandName(java.lang.String phone, java.lang.String sms, java.lang.String sender, java.lang.String username, java.lang.String password) {
        com.gk.htc.sendMT.CMC.HTC.SMS service = new com.gk.htc.sendMT.CMC.HTC.SMS();
        com.gk.htc.sendMT.CMC.HTC.SMSSoap port = service.getSMSSoap12();
        return port.sendSMSBrandName(phone, sms, sender, username, password);
    }
}
