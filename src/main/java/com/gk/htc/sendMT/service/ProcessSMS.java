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
import com.gk.htc.ahp.brand.entity.RouteTable;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;

/**
 * Xu Ly http Request
 *
 * @author NGOC LONG
 */
public class ProcessSMS {

    static final Logger logger = Logger.getLogger(ProcessSMS.class);
    public static final int EXPIRE_TPS = 1000; // In mili second
    public static final int EXPIRE_MSG = 5 * 60 * 1000;
    public static final int EXPIRE_TRANSID = 5 * 60 * 1000;
    private static boolean CREATE_TRAFFIC = false;

    public ProcessSMS() {

    }

    public ProcessSMS(String func, String reqType) {
        this.func = func;
        this.reqType = reqType;
    }

    public static enum CODE {

        REJECT(-2, "REJECT"),
        EXCEPTION(-1, "Unknow Exception Service"),
        ERROR(0, "Sending Fail"),
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
        INVALID_XML_DATA(17, "Invalid XML Data"),
        INVALID_JSON_DATA(18, "Invalid Json Data"),
        //        OVER_TPS(19, "Oer TPS"),
        RECEIVED(99, "RECEIVED") //--
        ;
        public int val;
        public String mess;
        private String result;

        public String getResult() {
            return result;
        }

        private void setResult(int val, String mess) {
            result = val + "." + mess;
        }

        private CODE(int val, String mess) {
            this.val = val;
            this.mess = mess;
            setResult(val, mess);
        }
    }
    //--
    String ip;
    String host;
    String user;
    String pass;
    String phone;
    String brandName;
    String func;
    String mess;
    String tranId;
    String scheduleTime;        // For Wait Send
    String reqType;        // MediaType.APPLICATION_FORM_URLENCODED MediaType.TEXT_XML MediaType.APPLICATION_JSON

    public CODE process() {
        DoWork working = new DoWork();
        // Tu Gan Type theo Funtion cua minh quy dinh
        int type = BrandLabel.TYPE.CSKH.val;
//        if (func.equalsIgnoreCase("QC")) {
//            type = BrandLabel.TYPE.QC.val;
//        }
//        String info = "user:" + user + "| pass:" + pass + "| phone:" + phone + "| brandName:" + brandName + "| type:" + func;
//        info += "| mess:" + mess + "| tranId:" + tranId + "| ip:" + ip + "| host:" + host;
        //--
        SmsBrandQueue oneReqQueue = new SmsBrandQueue();
//        oneReqQueue.setExtenInfo(info);
        //--
        oneReqQueue.setRequestTime(DateProc.createTimestamp());
        phone = SMSUtils.PhoneTo84(phone);
        oneReqQueue.setPhone(SMSUtils.PhoneTo84(phone));
        oneReqQueue.setLabel(brandName);
        //--
        oneReqQueue.setType(type);                              // CSKH/QC
        oneReqQueue.setMessage(mess);
        oneReqQueue.setTranId(tranId);                          //  info of Customer
        oneReqQueue.setSystemId(UniqueID.getId(phone));         // Mac dinh sinh ra boi SYSTEM
        oneReqQueue.setUserSender(user);                        // Set truoc trong truong hop sai user hoac pass
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
            String operByPhone = SMSUtils.buildMobileOperator(phone);
            oneReqQueue.setOper(operByPhone);
            int totalMsg = 0;

            totalMsg = SMSUtils.countSmsBrandCSKH(mess, operByPhone);
//            }
//            if (type == BrandLabel.TYPE.QC.val) {
//                totalMsg = SMSUtils.countSmsBrandQC(mess, operByPhone);
//            }
            oneReqQueue.setTotalSms(totalMsg);
            if (totalMsg == SMSUtils.REJECT_MSG_LENG) {
                oneReqQueue.setTotalSms(SMSUtils.countFast(mess));
                oneReqQueue.setResult(CODE.MSG_LENGTH_NOT_VALID.val);
                oneReqQueue.setErrorInfo(CODE.MSG_LENGTH_NOT_VALID.mess + "|length=" + SMSUtils.countFast(mess));
                return CODE.MSG_LENGTH_NOT_VALID;
            }
            oneReqQueue.setTotalSms(totalMsg);
            //--

            Account acc = new Account();
            // Account Dai ly
            acc = acc.checkLogin(user, pass);
            oneReqQueue.setUserSender(user);        // Addition
            if (acc == null) {
                oneReqQueue.setResult(CODE.LOGIN_FAIL.val);
                oneReqQueue.setErrorInfo(CODE.LOGIN_FAIL.mess + " |u=" + user + "|p=" + pass);
                return CODE.LOGIN_FAIL;
            }
            oneReqQueue.setCpCode(acc.getCpCode());
            //TAM THOI STOP
//            if (AppStart.memcache.overTPS(acc)) {
//                oneReqQueue.setResult(CODE.OVER_TPS.val);
//                oneReqQueue.setErrorInfo(CODE.OVER_TPS.mess + "|tps=" + acc.getTps());
//                return CODE.OVER_TPS;
//            }
            //--Check Lengh
            if (Tool.checkNull(mess)) {
                oneReqQueue.setResult(CODE.MESSAGE_NULL_OR_EMPTY.val);
                oneReqQueue.setErrorInfo("|Message Length invalid [Length=null] | " + CODE.MESSAGE_NULL_OR_EMPTY.mess);
                return CODE.MESSAGE_NULL_OR_EMPTY;
            }
            //--
//            if (type == BrandLabel.TYPE.CSKH.val) {
            // Chu yeu chay thang nay

            if (SMSUtils.isUnicode(mess)) {
                oneReqQueue.setResult(CODE.UNICODE_MESSAGE.val);
                oneReqQueue.setErrorInfo(CODE.UNICODE_MESSAGE.mess);
                return CODE.UNICODE_MESSAGE;
            }
            boolean phoneValid = SMSUtils.validPhoneVN(phone);
            if (!phoneValid || Tool.getLength(phone) > 12) {
                oneReqQueue.setResult(CODE.SENDPHONE_INVALID.val);
                oneReqQueue.setErrorInfo("|Send Phone Invalid [phone=" + phone + "] | " + CODE.SENDPHONE_INVALID.mess + "|ph=" + phone);
                return CODE.SENDPHONE_INVALID;
            }
            boolean dupMsg = AppStart.memcache.checkDuplicateMsg(oneReqQueue);
            if (dupMsg) {
                oneReqQueue.setResult(CODE.SAME_CONTENT_SHORT_TIME.val);
                oneReqQueue.setErrorInfo(CODE.SAME_CONTENT_SHORT_TIME.mess);
                return CODE.SAME_CONTENT_SHORT_TIME;
            }
            boolean dupTransId = AppStart.memcache.checkDuplicateTransId(oneReqQueue);
            if (dupTransId) {
                oneReqQueue.setResult(CODE.DUPLICATE_TRANS_ID.val);
                oneReqQueue.setErrorInfo(CODE.DUPLICATE_TRANS_ID.mess + "|tran=" + tranId);
                return CODE.DUPLICATE_TRANS_ID;
            }
            mess = StringUtils.replaceMultiWhiteSpace(mess);
            oneReqQueue.setMessage(mess);
            if (acc.getStatus() != Account.STATUS.ACTIVE.val) {
                oneReqQueue.setResult(CODE.ACC_LOCKED.val);
                oneReqQueue.setErrorInfo(CODE.ACC_LOCKED.mess + " |u=" + user);
                return CODE.ACC_LOCKED;
            } else {
                if (!acc.validIP(ip)) {
                    oneReqQueue.setResult(CODE.IP_SER_NOT_ALLOW.val);
                    oneReqQueue.setErrorInfo(CODE.IP_SER_NOT_ALLOW.mess + " |ip=" + ip);
                    return CODE.IP_SER_NOT_ALLOW;
                }
//                if (!validFunction(func)) {
//                    oneReqQueue.setResult(CODE.UNKNOW_SERVICE.val);
//                    oneReqQueue.setErrorInfo(CODE.UNKNOW_SERVICE.mess + "|" + info);
//                    return CODE.UNKNOW_SERVICE;
//                }
                //-- Laybrand
                BrandLabel brand = BrandLabel.findFromCache(acc.getUserName(), brandName);
                if (brand == null) {
                    oneReqQueue.setResult(CODE.BRAND_NOT_AVTIVE.val);
                    oneReqQueue.setErrorInfo(CODE.BRAND_NOT_AVTIVE.mess + " |br=" + brandName);
                    return CODE.BRAND_NOT_AVTIVE;
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
                    oneReqQueue.setErrorInfo(CODE.SMS_TELCO_NOT_ALLOW.mess + " |oper=" + operByPhone);
                    return CODE.SMS_TELCO_NOT_ALLOW;
                }
                //--
                oneReqQueue.setResult(CODE.RECEIVED.val);                       // Ket qua Trong Log income
                oneReqQueue.setErrorInfo(CODE.RECEIVED.mess);
                // Clone ra De gui va log income khong bi anh huong
                SmsBrandQueue queueSend = oneReqQueue.clone();
                AppStart.sendPrimaryTask.addToqueue(queueSend);
                responseCounter.incrementAndGet();

                // Return theo tai lieu la val.message
                return CODE.SUCCESS;
            }
            // -----------
        } catch (Exception e) {
            oneReqQueue.setResult(CODE.EXCEPTION.val);
            oneReqQueue.setErrorInfo(CODE.EXCEPTION.mess + " | ExMessage=" + e.getMessage());
            // Log Brand Message Income
            logger.error(Tool.getLogMessage(e));
            return CODE.ERROR;
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
            this.setName("[==>ProcessSMS Traffic [" + DateProc.createTimestamp() + "]");
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

    public String buiildRsp(CODE code) {
        switch (reqType) {
            case MediaType.APPLICATION_XML:
                return buildRspXML(code);
            case MediaType.APPLICATION_JSON:
                return buildRspJson(code);
            case MediaType.APPLICATION_FORM_URLENCODED:
                return buildHttp(code);
            default:
                return buildHttp(code);  // Mac Dinh coi nhu Request Http
        }
    }

    private static String buildRspXML(CODE code) {
        String str = "<response>"
                + "<code>" + code.val + "</code>"
                + "<desc>" + code.mess + "</desc>"
                + "</response>";
        return str;
    }

    private static String buildRspJson(CODE code) {
        String str = "{"
                + "\"code\":\"" + code.val + "\""
                + ",\"desc\":\"" + code.mess + "\""
                + "}";
        return str;
    }

    private String buildHttp(CODE code) {
        String str = code.getResult();
        return str;
    }

//    private boolean validFunction(String func) {
//        boolean result = Boolean.FALSE;
//        for (String onefunc : funtion) {
//            if (onefunc.equalsIgnoreCase(func) || "test".equals(func)) {
//                result = Boolean.TRUE;
//                break;
//            }
//        }
//        return result;
//    }
    // GET SET
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

}
