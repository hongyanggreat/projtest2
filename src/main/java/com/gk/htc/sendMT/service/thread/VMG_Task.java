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
import com.gk.htc.sendMT.VMG.ResultVMG;
import com.gk.htc.sendMT.VMG.SoapVMGUnicode;
import org.apache.log4j.Logger;

/**
 *
 * @author Centurion
 */
public class VMG_Task extends _AbstractThreadSend2Provider {

    static final Logger logger = Logger.getLogger(VMG_Task.class);
    //--
    private static final WorkQueue WORKQUEUE_VMG = new WorkQueue("WORKQUEUE_VMG", 10);
    private static final String QUEUE_NAME = "Q-->SEND_TO_VMG";

    public VMG_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_VMG.getMaxPoolSize();
        this.setName("VMG_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_VMG);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {

                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_VMG.execute(doSendBrand_runnable(oneQueueBr));
                }
                Thread.sleep(1000 / TPS);
            } catch (InterruptedException e) {
                logger.error(Tool.getLogMessage(e));
            }
        }
        StoreQueue();
        MonitorWorker.removeDemonName(this.getName());
    }

    //------
    @Override
    protected Runnable doSendBrand_runnable(final SmsBrandQueue oneQueue) {
        return new Runnable() {
            @Override
            public void run() {
                DoWork working = new DoWork();
                oneQueue.setTimeSend(DateProc.createTimestamp());
                try {
                    // Submit
                    ResultVMG resp = SoapVMGUnicode.BulkSendSms(oneQueue);
//                    ResultVMG resp = SoapVMG.BulkSendSms(oneQueue);
                    long delay = working.done();
                    oneQueue.setProcessTime(delay);
                    if (resp.getError_code() == CODE.SUCCESS.val) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                    } else if (resp.getError_code() == CODE.SAME_CONTENT_SHORT_TIME.val) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(getMessByCode(resp.getError_code()));
                    } else {
                        oneQueue.setResult(resp.getError_code());
                        oneQueue.setErrorInfo(getMessByCode(resp.getError_code()));
                    }
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom(" Send2VMGUnicode_Task:" + e.getMessage());
                    if (oneQueue.getRetry() > 3) {
                        logData(oneQueue);
                    } else {
                        SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                    }
                }
            }
        };
    }

    public static enum CODE {

        SUCCESS(0, "Success"), // Thuc chat la VMG
        SEND_ERROR(-1, "Sending error (Message content unicode character)"),
        AUTHEN_FAIL(100, "Authentication failure"),
        USER_DEACTIVED(101, "Authentication User is deactived"),
        USER_EXPIRE(102, "Authentication User is expired"),
        USER_LOCK(103, "Authentication User is locked"),
        TEM_NOT_ACTIVE(104, "Template not actived"),
        TEM_NOT_EXIST(105, "Template does not existed"),
        PHONE_BLACK_LIST(108, "Msisdn in blackList"),
        SAME_CONTENT_SHORT_TIME(304, "Send the same content in short time"),
        NOT_ENOUGH_MONEY(400, "Not enough money"),
        SYS_ERROR(900, "System is error"),
        LENG_SMS_ERROR(901, "Length of message is 612 with noneUnicode message and 266 with Unicode message"),
        PHONE_ERROR_MIN(902, "Number of msisdn must be > 0"),
        PHONE_ERROR_MAX(903, "Number of msisdn must be <= 1000"),
        BRAND_NOT_VALID(904, "Brandname is inactive"),
        // HTC Define
        ERR_PASER(-99, "HTC Paser result Error"),;
        ;
        public int val;
        public String mess;

        private CODE(int val, String mess) {
            this.val = val;
            this.mess = mess;
        }

        public static String getmessage(int val) {
            String str = "";
            CODE[] tmp = CODE.values();
            for (CODE one : tmp) {
                if (one.val == val) {
                    str = one.mess;
                }
            }
            return str;
        }
    }

    public static String getMessByCode(int val) {
        String mess = "";
        for (CODE one : CODE.values()) {
            if (one.val == val) {
                mess = one.mess;
                break;
            }
        }
        if (Tool.checkNull(mess)) {
            mess = "Unknow VAL:" + val;
        }
        return mess;
    }
}
