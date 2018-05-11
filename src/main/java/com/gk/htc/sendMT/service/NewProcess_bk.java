/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.service;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.DoWork;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.entity.UniqueID;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.StringUtils;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.Account;
import com.gk.htc.ahp.brand.entity.rest.RequestMessage;
import com.gk.htc.ahp.brand.entity.rest.ResponseMessage;
import com.gk.htc.ahp.brand.entity.RouteTable;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;

/**
 * Xu Ly http Request
 *
 * @author NGOC LONG
 */
public class NewProcess_bk {

    static final Logger logger = Logger.getLogger(NewProcess_bk.class);
    public static final int EXPIRE_TPS = 1; // In second
    public static final int EXPIRE_MSG = 5 * 60;
    public static final int EXPIRE_TRANSID = 5 * 60;
    private static boolean CREATE_TRAFFIC = false;
    private final RequestMessage reqMsg;

    public NewProcess_bk(RequestMessage reqMsg) {
        this.reqMsg = reqMsg;
    }

    public static enum CODE {
        REJECT(-2, "REJECT"),
        EXCEPTION(-1, "Unknow Exception Service"),
        ERROR(0, "Sending Fail"),
        SUCCESS(1, "Success"), // Tra ve ket qua cho KH khong duoc thay doi
        IP_SER_NOT_ALLOW(2, "Authentication IP failure"), // Sai IP
//        UNKNOW_SERVICE(3, "UnknowService"), // Sai user
//        ERROR_SERVICE_HTC(4, "Service Send SMS Error"),
        SENDPHONE_INVALID(5, "Send Phone Invalid"),
        LOGIN_FAIL(6, "Authentication login failure"),
        BRAND_NOT_AVTIVE(7, "Brand not Active"),
        SMS_TELCO_NOT_ALLOW(8, "Send SMS to Telco Not Allow"),
//        TEMP_NOT_VALID(9, "Template not valid"),
        MSG_LENGTH_NOT_VALID(10, "Message Length invalid"),
        UNICODE_MESSAGE(11, "Message Has Unicode Charactor"),
        MESSAGE_NULL_OR_EMPTY(12, "Message null or Empty"),
        ACC_LOCKED(13, "Account is locked"),
        SAME_CONTENT_SHORT_TIME(15, "The Same Content Short Time"),
        DUPLICATE_TRANS_ID(16, "The Same Trans_id on 5 Minus"),
        INVALID_XML_DATA(17, "Invalid XML Data"),
        INVALID_JSON_DATA(18, "Invalid Json Data"),
        OVER_TPS(19, "Over TPS"),
        RECEIVED(99, "RECEIVED") //--
        ;
        public int val;
        public String mess;

        private CODE(int val, String mess) {
            this.val = val;
            this.mess = mess;
        }
    }
    //--

    public ResponseMessage process() {
        DoWork working = new DoWork();
        ResponseMessage _resp = new ResponseMessage();

        int type = BrandLabel.TYPE.CSKH.val;
        //--
        SmsBrandQueue oneReqQueue = new SmsBrandQueue();
        oneReqQueue.setExtenInfo(reqMsg.getIp() + "|" + reqMsg.getHost());
        //--
        oneReqQueue.setRequestTime(DateProc.createTimestamp());
        String phone = SMSUtils.PhoneTo84(reqMsg.getPhone());
        //-->
        String operByPhone = SMSUtils.buildMobileOperator(phone);
        oneReqQueue.setOper(operByPhone);
        //-->
        oneReqQueue.setPhone(SMSUtils.PhoneTo84(phone));
        oneReqQueue.setLabel(reqMsg.getBrandName());
        //--
        oneReqQueue.setType(type);                      // CSKH/QC
        oneReqQueue.setMessage(reqMsg.getMess());
        oneReqQueue.setTranId(reqMsg.getTranId());                          //  info of Customer
        //--
        _resp.setTransId(reqMsg.getTranId());
        _resp.setOper(operByPhone);
        //--
        oneReqQueue.setSystemId(UniqueID.getId(phone));                     // Mac dinh sinh ra boi SYSTEM
        oneReqQueue.setUserSender(reqMsg.getUser());                        // Set truoc trong truong hop sai user hoac pass
        //--
        long delay = 1;
        oneReqQueue.setProcessTime(delay);
        oneReqQueue.setNode(MyConfig.LB_NODE);
        oneReqQueue.setSource(SmsBrandQueue.SOURCE.API.val);
        //-End Pram
        try {
            if (!CREATE_TRAFFIC) {
                CREATE_TRAFFIC = true;
                Traffic trf = new Traffic();
                trf.start();
            }
            requestCounter.incrementAndGet();
            // -- Dem tin long
            Account acc = new Account();
            // Account Dai ly
            acc = acc.checkLogin(reqMsg.getUser(), reqMsg.getPass());
            oneReqQueue.setUserSender(reqMsg.getUser());        // Addition
            if (acc == null) {
                oneReqQueue.setResult(CODE.LOGIN_FAIL.val);
                oneReqQueue.setErrorInfo(CODE.LOGIN_FAIL.mess + " |u" + reqMsg.getUser() + "|p=" + reqMsg.getPass());
                _resp.setCode(CODE.LOGIN_FAIL.val);
                _resp.setMessage(CODE.LOGIN_FAIL.mess);
                return _resp;
            }
            oneReqQueue.setCpCode(acc.getCpCode());

            if (!acc.validIP(reqMsg.getIp())) {
                oneReqQueue.setResult(CODE.IP_SER_NOT_ALLOW.val);
                oneReqQueue.setErrorInfo(CODE.IP_SER_NOT_ALLOW.mess + " |ip=" + reqMsg.getIp());
                _resp.setCode(CODE.IP_SER_NOT_ALLOW.val);
                _resp.setMessage(CODE.IP_SER_NOT_ALLOW.mess);
                return _resp;
            }

            if (AppStart.memcache.overTPS(acc)) {
                oneReqQueue.setResult(CODE.OVER_TPS.val);
                oneReqQueue.setErrorInfo(CODE.OVER_TPS.mess + "|tps=" + acc.getTps());
                _resp.setCode(CODE.OVER_TPS.val);
                _resp.setMessage(CODE.OVER_TPS.mess);
                return _resp;
            }
            if (AppStart.memcache.checkDuplicateMsg(oneReqQueue)) {
                oneReqQueue.setResult(CODE.SAME_CONTENT_SHORT_TIME.val);
                oneReqQueue.setErrorInfo(CODE.SAME_CONTENT_SHORT_TIME.mess);
                _resp.setCode(CODE.SAME_CONTENT_SHORT_TIME.val);
                _resp.setMessage(CODE.SAME_CONTENT_SHORT_TIME.mess);
                return _resp;
            }
            if (AppStart.memcache.checkDuplicateTransId(oneReqQueue)) {
                oneReqQueue.setResult(CODE.DUPLICATE_TRANS_ID.val);
                oneReqQueue.setErrorInfo(CODE.DUPLICATE_TRANS_ID.mess + "|tran" + reqMsg.getTranId());
                _resp.setCode(CODE.DUPLICATE_TRANS_ID.val);
                _resp.setMessage(CODE.DUPLICATE_TRANS_ID.mess);
                return _resp;
            }
            //--Check Lengh
            if (Tool.checkNull(reqMsg.getMess())) {
                oneReqQueue.setResult(CODE.MESSAGE_NULL_OR_EMPTY.val);
                oneReqQueue.setErrorInfo("|Message Length invalid [Length=null] | " + CODE.MESSAGE_NULL_OR_EMPTY.mess);
                _resp.setCode(CODE.MESSAGE_NULL_OR_EMPTY.val);
                _resp.setMessage(CODE.MESSAGE_NULL_OR_EMPTY.mess);
                return _resp;
            }
            //--
            // CHECK MESSAGE UNICODE
            int totalMsg = 0;
            int lengthOver = 0;
            oneReqQueue.setDataEncode(reqMsg.getDataEncode());
            if (reqMsg.getDataEncode()!= 1) { // STRING 
                //TH NOT UNICODE 
//                System.out.println("=============== TH KHONG UNICODE ===============");
                totalMsg = SMSUtils.countSmsBrandCSKH(reqMsg.getMess(), operByPhone);
                oneReqQueue.setTotalSms(totalMsg);
                _resp.setTotalSMS(totalMsg);
                if (totalMsg <= 0) {
                    lengthOver = SMSUtils.countFast(reqMsg.getMess());
                    oneReqQueue.setTotalSms(lengthOver);
                    _resp.setTotalSMS(lengthOver);
                    oneReqQueue.setResult(CODE.MSG_LENGTH_NOT_VALID.val);
                    oneReqQueue.setErrorInfo(CODE.MSG_LENGTH_NOT_VALID.mess + " |length=" + lengthOver);
                    _resp.setCode(CODE.MSG_LENGTH_NOT_VALID.val);
                    _resp.setMessage(CODE.MSG_LENGTH_NOT_VALID.mess);
                    return _resp;
                }
                if (SMSUtils.isUnicode(reqMsg.getMess())) {
                    oneReqQueue.setResult(CODE.UNICODE_MESSAGE.val);
                    oneReqQueue.setErrorInfo(CODE.UNICODE_MESSAGE.mess);
                    _resp.setCode(CODE.UNICODE_MESSAGE.val);
                    _resp.setMessage(CODE.UNICODE_MESSAGE.mess);
                    return _resp;
                }

            } else {
                // TH UNICODE
//                System.out.println("=============== TH UNICODE ===============");
                totalMsg = SMSUtils.countSmsBrandCSKHUnicode(reqMsg.getMess());
                oneReqQueue.setTotalSms(totalMsg);
                _resp.setTotalSMS(totalMsg);
                if (totalMsg <= 0) {
                    lengthOver = SMSUtils.countFastUnicode(reqMsg.getMess());
                    oneReqQueue.setTotalSms(lengthOver);
                    _resp.setTotalSMS(lengthOver);
                    oneReqQueue.setResult(CODE.MSG_LENGTH_NOT_VALID.val);
                    oneReqQueue.setErrorInfo(CODE.MSG_LENGTH_NOT_VALID.mess + " |length=" + lengthOver);
                    _resp.setCode(CODE.MSG_LENGTH_NOT_VALID.val);
                    _resp.setMessage(CODE.MSG_LENGTH_NOT_VALID.mess);
                    return _resp;
                }
            }

            String mess = StringUtils.replaceMultiWhiteSpace(oneReqQueue.getMessage());
            oneReqQueue.setMessage(mess);

            boolean phoneValid = SMSUtils.validPhoneVN(phone);
            if (!phoneValid || Tool.getLength(phone) > 12) {
                oneReqQueue.setResult(CODE.SENDPHONE_INVALID.val);
                oneReqQueue.setErrorInfo(CODE.SENDPHONE_INVALID.mess + "|ph=" + reqMsg.getPhone());
                _resp.setCode(CODE.SENDPHONE_INVALID.val);
                _resp.setMessage(CODE.SENDPHONE_INVALID.mess);
                return _resp;
            }

            if (acc.getStatus() != Account.STATUS.ACTIVE.val) {
                oneReqQueue.setResult(CODE.ACC_LOCKED.val);
                oneReqQueue.setErrorInfo(CODE.ACC_LOCKED.mess + " |u=" + reqMsg.getUser());
                _resp.setCode(CODE.ACC_LOCKED.val);
                _resp.setMessage(CODE.ACC_LOCKED.mess);
                return _resp;
            } else {

                //-- Laybrand
                BrandLabel brand = BrandLabel.findFromCache(acc.getUserName(), reqMsg.getBrandName());
                if (brand == null) {
                    oneReqQueue.setResult(CODE.BRAND_NOT_AVTIVE.val);
                    oneReqQueue.setErrorInfo(CODE.BRAND_NOT_AVTIVE.mess + " |br=" + reqMsg.getBrandName());
                    _resp.setCode(CODE.BRAND_NOT_AVTIVE.val);
                    _resp.setMessage(CODE.BRAND_NOT_AVTIVE.mess);
                    return _resp;
                }
                // OPTION TELCO
                oneReqQueue.setOptString(brand.getOptionTelco());
                //--
                RouteTable route = brand.getRoute();
                // Lay ra Huong Gui
                String _sendTo = route.getSendTo(operByPhone, type);
                oneReqQueue.setSendTo(_sendTo);
                // GROUP BY TELCO
                String group = route.getGroup(operByPhone);
                oneReqQueue.setBrGroup(group);
                //--
                boolean operApproved = route.checkRole(operByPhone, type);
                if (!operApproved) {
                    oneReqQueue.setResult(CODE.SMS_TELCO_NOT_ALLOW.val);
                    oneReqQueue.setErrorInfo(CODE.SMS_TELCO_NOT_ALLOW.mess + " |oper" + operByPhone);
                    _resp.setCode(CODE.SMS_TELCO_NOT_ALLOW.val);
                    _resp.setMessage(CODE.SMS_TELCO_NOT_ALLOW.mess);
                    return _resp;
                }
                //--
                oneReqQueue.setResult(CODE.RECEIVED.val);                       // Ket qua Trong Log income
                oneReqQueue.setErrorInfo(CODE.RECEIVED.mess);
                // Clone ra De gui va log income khong bi anh huong
                SmsBrandQueue queueSend = oneReqQueue.clone();
                AppStart.sendPrimaryTask.addToqueue(queueSend);
                responseCounter.incrementAndGet();

                // Return theo tai lieu la val.message
                _resp.setCode(CODE.SUCCESS.val);
                _resp.setMessage(CODE.SUCCESS.mess);
                return _resp;
            }
            // -----------
        } catch (Exception e) {
            oneReqQueue.setResult(CODE.EXCEPTION.val);
            oneReqQueue.setErrorInfo(CODE.EXCEPTION.mess + " | ExMessage=" + e.getMessage() + " | " + reqMsg.toJsonStr());
            // Log Brand Message Income
            logger.error(Tool.getLogMessage(e));
            _resp.setCode(CODE.ERROR.val);
            _resp.setMessage(CODE.ERROR.mess);
            return _resp;
        } finally {
            delay = working.done();
            if (maxDelay.get() < delay) {
                maxDelay.set(delay);
            }
            oneReqQueue.setProcessTime(delay);
            // TODO 
            logDataInCome(oneReqQueue);
        }
    }

    public static void shutDownTraffic() {
        exit.set(true);
        CREATE_TRAFFIC = false;
    }
    private static final AtomicBoolean exit = new AtomicBoolean();
    private static final AtomicInteger requestCounter = new AtomicInteger();
    private static final AtomicInteger totalRequestCounter = new AtomicInteger();

    private static final AtomicLong maxDelay = new AtomicLong();

    private static final AtomicInteger responseCounter = new AtomicInteger();
    private static final AtomicInteger totalResponseCounter = new AtomicInteger();

    private class Traffic extends Thread {

        public Traffic() {
            this.setName("[==>NewProcess Traffic [" + DateProc.createTimestamp() + "]");
            MonitorWorker.addDemonName(this.getName());
        }

        @Override
        public void run() {
            Tool.debug("[===> Starting traffic watcher...exit.get() =" + exit.get() + "|CREATE_TRAFFIC=" + CREATE_TRAFFIC);
            while (!exit.get()) {
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                }
                int requestPerSecond = requestCounter.getAndSet(0);
                int responsePerSecond = responseCounter.getAndSet(0);

                long maxDelayPerSecond = maxDelay.getAndSet(0);

                totalRequestCounter.addAndGet(requestPerSecond);
                int total = totalResponseCounter.addAndGet(responsePerSecond);
                Tool.debug("[" + MyConfig.LB_NODE + ": Request/ResponseSuccess per 60 second : " + requestPerSecond + "/" + responsePerSecond + " of " + total + " maxDelay=" + maxDelayPerSecond + "---]");
                // Log de xem co thang nao bi delay lon khong
                MyLog.debug("[---Request/ResponseSuccess per 60 second : " + requestPerSecond + "/" + responsePerSecond + " of " + total + " maxDelay=" + maxDelayPerSecond + "---]");

            }
            MonitorWorker.removeDemonName(this.getName());
        }
    }

    private static void logDataInCome(SmsBrandQueue oneReqQueue) {
        AppStart.log_incomeTask.addToqueue(oneReqQueue);
        // Quan Diem la Loginfo Request het vi Day khong phai Loi He Thong
        MyLog.logIncome(SmsQueueDao.toStringJson(oneReqQueue));
    }
}
