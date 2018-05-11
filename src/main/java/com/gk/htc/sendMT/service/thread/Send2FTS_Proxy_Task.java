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
import com.gk.htc.sendMT.FTS.ClientFTS_Proxy;
import com.gk.htc.sendMT.FTS.ResponseProxy;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class Send2FTS_Proxy_Task extends _AbstractThreadSend2Provider {

    private static final Logger logger = Logger.getLogger(Send2FTS_Proxy_Task.class);
    private static final WorkQueue WORKQUEUE_FTS = new WorkQueue("WORKQUEUE_FTS", 100);
    private static final String QUEUE_NAME = "Q-->SEND_TO_FTS";

    public Send2FTS_Proxy_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_FTS.getMaxPoolSize();
        this.setName("FTS_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_FTS);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_FTS.execute(doSendBrand_runnable(oneQueueBr));
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
                    String[] resp = ClientFTS_Proxy.sendOneQueue(oneQueue);
                    long delay = working.done();
                    String info = oneQueue.getLabel() + " -> [" + oneQueue.getSendTo() + "] d=" + delay;
                    Tool.debug(info);
                    oneQueue.setProcessTime(delay);
                    if (resp[0].equals(1)) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG);
                    } else {
                        oneQueue.setResult(Tool.getInt(resp[0]));
                        oneQueue.setErrorInfo(resp[1]);
                    }
                    //--
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom(" Send2_FTS_Task:" + e.getMessage());
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

        EXCEPTION(-1, "Unknow Exception Service"),
        SUCCESS(1, "Success"), // Tra ve ket qua cho KH khong duoc thay doi
        IP_SER_NOT_ALLOW(2, "Authentication IP failure"), // Sai IP
        UNKNOW_SERVICE(3, "UnknowService"), // Sai user
        ERROR_SERVICE_HTC(4, "Service Send SMS Error"),
        SENDPHONE_INVALID(5, "Send Phone Invalid"),
        LOGIN_FAIL(6, "Authentication login failure"),
        BRAND_NOT_AVTIVE(7, "Brand not Active"),
        SMS_TELCO_NOT_ALLOW(8, "Send SMS to Telco Not Allow"),
        TEMP_NOT_VALID(9, "Template not valid"),
        MSG_LENGTH_NOT_VALID(10, "Message Length invalid"),
        UNICODE_MESSAGE(11, "Message Has Unicode Charactor"),
        MESSAGE_NULL_OR_EMPTY(12, "Message null or Empty"),
        ACC_LOCKED(13, "Account is locked"),
        SAME_CONTENT_SHORT_TIME(15, "The Same Content Short Time"),
        DUPLICATE_TRANS_ID(16, "The Same Trans_id on 5 Minus"),
        RESPONSE_NOTVALID(-99, "The Same Trans_id on 5 Minus"),;
        public int val;
        public String mess;
        private String result;

        public String getResult() {
            return result;
        }

        private void setResult(int val, String mess) {
            result = val + "." + mess;
        }

        public static String getMess(int val) {
            String _result = "Unknow";
            for (STATUS one : STATUS.values()) {
                if (one.val == val) {
                    _result = one.mess;
                    break;
                }
            }
            return _result;
        }

        private STATUS(int val, String mess) {
            this.val = val;
            this.mess = mess;
            setResult(val, mess);
        }
    }

}
