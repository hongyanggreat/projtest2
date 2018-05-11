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
public class VNPTMediaUnicode_Task extends _AbstractThreadSend2Provider {

    final Logger logger = Logger.getLogger(VNPTMediaUnicode_Task.class);
    private static final String USER = "htc-sms2";
    private static final String PASS = "b50f5e5480e4d2e953ff924e38d30be3";
    private static final WorkQueue WORKQUEUE_VNPT_MEDIA_UNICODE_MAIN = new WorkQueue("WORKQUEUE_VNPT_MEDIA_UNICODE_MAIN", 10);
    private static final String QUEUE_NAME = "Q-->SEND_TO_VNPT_MEDIA_MAIN";

    public VNPTMediaUnicode_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_VNPT_MEDIA_UNICODE_MAIN.getMaxPoolSize();
        this.setName("VNPTMedia_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_VNPT_MEDIA_UNICODE_MAIN);
    }

    public static enum STATUS {

        SUCCESS(0, "Success"),
        LOSE_INPUT_DATA(-1, "Nhập thiếu thông số."),
        WRONG_CONTENT_TYPE(-2, "Sai contentType. Truyền mặc định là 0."),
        LOGIN_FAIL(-3, "Sai username hoặc password"),
        NOT_HAS_RULE(-4, "Không có quyền upload tin qua webservice này."),
        WRONG_LABEL(-5, "Sai serviceID"),
        WRONG_PHONE(-6, "Số điện thoại không đúng định dạng"),
        ERROR_1(-7, "Có lỗi trong quá trình xữ lý của VASC."),
        ERROR_2(-8, "Có lỗi trong quá trình xữ lý của VASC."), //--
        ;
        public int val;
        public String mess;

        private STATUS(int val, String mess) {
            this.val = val;
            this.mess = mess;
        }

        public static String getmessage(int val) {
            String str = "Unknow Result VNPT VAL:" + val;
            for (STATUS one : STATUS.values()) {
                if (one.val == val) {
                    str = one.mess;
                    break;
                }
            }
            return str;
        }
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {

                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_VNPT_MEDIA_UNICODE_MAIN.execute(doSendBrand_runnable(oneQueueBr));
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

                    // neu tin nhan tieng viet set lai gia tri == 8
//                    System.out.println("getDataEndCode VNPTMediaUnicode: BAN DAU :" + oneQueue.getDataEndCode());
//                    if (oneQueue.getDataEndCode() == 1) {
//                        oneQueue.setDataEndCode(8);
//                        System.out.println("GUI TIN NHAN TIENG VIET VNPTMediaUnicode: SAU KHI SET LẠI GIA TRI:" + oneQueue.getDataEndCode());
//                    }

                    int repResult = doSendMT(oneQueue.getPhone(), oneQueue.getLabel(), oneQueue.getMessage(), oneQueue.getDataEncode());
                    // Gui Xong
                    String requestIdSms = " : requestId SMS là : " + repResult;
                    // Nêu ket qua lon hon 0 thi gan lai mac dinh thanh cong ==0
                    if (repResult > 0) {
                        repResult = 0;
                    }
                    long delay = working.done();
                    oneQueue.setProcessTime(delay);
                    if (repResult == STATUS.SUCCESS.val) {
                        oneQueue.setResult(SUCCESS_INT);
                        oneQueue.setErrorInfo(SUCCESS_MSG + requestIdSms);
                    } else {
                        oneQueue.setResult(repResult);
                        oneQueue.setErrorInfo(STATUS.getmessage(repResult));
                    }
                    logData(oneQueue);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                    oneQueue.setRetry();
                    oneQueue.setResult(EXCEPTION);
                    oneQueue.setErrorInfo(e.getMessage());
                    oneQueue.setCacheFrom("Send2_VNPTMedia_MAIN_Task:" + e.getMessage());
                    if (oneQueue.getRetry() > 3) {
                        logData(oneQueue);
                    } else {
                        SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                    }
                }
            }
        };
    }

    private static int doSendMT(String msisdn, String brandname, String msgbody, int dataCoding) {
        // TODO Cho Nay Lam tam the thoi
//        String msg = Tool.replace(msgbody, "*", "x");
        return uploadSMS(
                USER,
                PASS,
                brandname,
                msisdn,
                "0", //- "0"” : Tin nhắn Text (short sms+Long SMS).
                "2", // "0"-Không xác định "1"-Quảng cáo, "2"- Chăm sóc khách hàng                
                msgbody,
                dataCoding==1?8:0 // Gửi tiếng việt có dấu
        );
    }

    private static int uploadSMS(java.lang.String username, java.lang.String password, java.lang.String serviceId, java.lang.String userId, java.lang.String contentType, java.lang.String serviceKind, java.lang.String infor, java.lang.Integer dataCoding) {
        com.gk.htc.sendMT.VNPTMediaUnicode.BrandNameWSService service = new com.gk.htc.sendMT.VNPTMediaUnicode.BrandNameWSService();
        com.gk.htc.sendMT.VNPTMediaUnicode.BrandNameWS port = service.getBrandNameWSPort();
        return port.uploadSMS(username, password, serviceId, userId, contentType, serviceKind, infor, dataCoding);
    }
}
