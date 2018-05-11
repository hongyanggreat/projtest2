/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.resource.http;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.DoWork;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.StringUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.Account;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.entity.RouteTable;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.entity.UniqueID;
import java.util.HashMap;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 * API VIET RIENG CHO TPCOM Ko co TranId ma Tra TranId ve cho KH
 *
 * @author PHAM TUAN
 */
@Path("/tpcom/brand")
public class ServiceBrandTPCom {

    static final Logger logger = Logger.getLogger(ServiceBrandTPCom.class);
    private static final String[] funtion = {"QC", "CSKH"};

    private static enum CODE {

        EXCEPTION(-1, "Unknow Exception Service"),
        ERROR(0, "Sending Fail"),
        SUCCESS(1, "Success"), // Tra ve ket qua cho KH khong duoc thay doi
        REJECT(-2, "REJECT"),
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
        RECEIVED(99, "RECEIVED") //--
        ;
        public int val;
        public String mess;

        public String getResult(String tranId) {
            return val + "." + tranId;
        }

        private CODE(int val, String mess) {
            this.val = val;
            this.mess = mess;
        }
    }

    private Response processRequest(HashMap<String, String> properties, String ip) {
        DoWork working = new DoWork();
        String host = Tool.getString(properties.get("host"));
        String user = Tool.getString(properties.get("user"));
        String pass = Tool.getString(properties.get("pass"));
        String phone = Tool.getString(properties.get("phone"));
        String brandName = Tool.getString(properties.get("brandName"));
        String function = Tool.getString(properties.get("function"));
        String mess = Tool.getString(properties.get("mess"));
        String tranId = UniqueID.getId(user);
        // Tu Gan Type theo Funtion cua minh quy dinh
        int type = BrandLabel.TYPE.CSKH.val;
        if (function.equalsIgnoreCase("QC")) {
            type = BrandLabel.TYPE.QC.val;
        }
        String info = "user:" + user + "| pass:" + pass + "| phone:" + phone + "| brandName:" + brandName + "| type:" + function;
        info += "| mess:" + mess + "| tranId:" + tranId + "| ip:" + ip + "| host:" + host;
        //--
        SmsBrandQueue oneReqQueue = new SmsBrandQueue();
        oneReqQueue.setExtenInfo(info);
        //--
        oneReqQueue.setRequestTime(DateProc.createTimestamp());
        phone = SMSUtils.PhoneTo84(phone);
        oneReqQueue.setPhone(phone);
        oneReqQueue.setLabel(brandName);

        oneReqQueue.setType(type);                              // CSKH/QC
        oneReqQueue.setMessage(mess);
        oneReqQueue.setTranId(tranId);                          //  info Trả về cho KH
        oneReqQueue.setSystemId(UniqueID.getId(phone));         // Mac dinh sinh ra boi SYSTEM
        oneReqQueue.setUserSender(user);
        //--
        long delay = working.done();
        oneReqQueue.setProcessTime(delay);
        oneReqQueue.setNode(MyConfig.LB_NODE);
        oneReqQueue.setSource(SmsBrandQueue.SOURCE.API.val);
        //-End Pram
        try {
            // -- Dem tin long
            int totalMsg = 0;
            //--
            String operByPhone = SMSUtils.buildMobileOperator(phone);
            oneReqQueue.setOper(operByPhone);
            //--Check Lengh
            if (Tool.checkNull(mess)) {
                // Log Brand Message Income
                oneReqQueue.setResult(CODE.MESSAGE_NULL_OR_EMPTY.val);
                oneReqQueue.setErrorInfo(info + "| Message Length invalid [Length=null]|" + CODE.MESSAGE_NULL_OR_EMPTY.mess + "|delay=" + delay);
                return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.MESSAGE_NULL_OR_EMPTY.getResult(tranId)).build();
            }
            if (SMSUtils.isUnicode(mess)) {
                oneReqQueue.setResult(CODE.UNICODE_MESSAGE.val);
                oneReqQueue.setErrorInfo(info + "|" + CODE.UNICODE_MESSAGE.mess + "|delay=" + delay);
                logger.error(info + "|" + CODE.UNICODE_MESSAGE.mess + "|delay=" + delay);
                return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.UNICODE_MESSAGE.getResult(tranId)).build();
            }
            //--
            if (type == BrandLabel.TYPE.CSKH.val) {
                // Chu yeu chay thang nay
                totalMsg = SMSUtils.countSmsBrandCSKH(mess, operByPhone);
            }
            if (type == BrandLabel.TYPE.QC.val) {
                totalMsg = SMSUtils.countSmsBrandQC(mess, operByPhone);
            }
            if (totalMsg == SMSUtils.REJECT_MSG_LENG) {
                oneReqQueue.setTotalSms(SMSUtils.countFast(mess));
                oneReqQueue.setResult(CODE.MSG_LENGTH_NOT_VALID.val);
                oneReqQueue.setErrorInfo(CODE.MSG_LENGTH_NOT_VALID.mess + "|" + info);
                return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.MSG_LENGTH_NOT_VALID.getResult(tranId)).build();
            }
            oneReqQueue.setTotalSms(totalMsg);
            boolean phoneValid = SMSUtils.validPhoneVN(phone);
            if (!phoneValid || Tool.getLength(phone) > 12) {
                // TODO DEV
                oneReqQueue.setResult(CODE.SENDPHONE_INVALID.val);
                oneReqQueue.setErrorInfo(info + "| Send Phone Invalid [phone=" + phone + "]|" + CODE.SENDPHONE_INVALID.mess + "|delay=" + delay);
                return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.SENDPHONE_INVALID.mess).build();
            }
            boolean dupMsg = AppStart.memcache.checkDuplicateMsg(oneReqQueue);
            if (dupMsg) {
                oneReqQueue.setResult(CODE.SAME_CONTENT_SHORT_TIME.val);
                oneReqQueue.setErrorInfo(info + "|" + CODE.SAME_CONTENT_SHORT_TIME.mess + "|delay=" + delay);
                logDataInCome(oneReqQueue);
                return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.SAME_CONTENT_SHORT_TIME.getResult(tranId)).build();
            }
            mess = StringUtils.replaceMultiWhiteSpace(mess);
            oneReqQueue.setMessage(mess);

            Account accDao = new Account();
            // Account Dai ly
            accDao = accDao.checkLogin(user, pass);
            if (accDao == null) {
                oneReqQueue.setResult(CODE.LOGIN_FAIL.val);
                oneReqQueue.setErrorInfo(CODE.LOGIN_FAIL.mess + "|" + info);
                return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.LOGIN_FAIL.getResult(tranId)).build();
            }
            oneReqQueue.setCpCode(accDao.getCpCode());
            if (accDao.getStatus() != Account.STATUS.ACTIVE.val) {
                oneReqQueue.setResult(CODE.ACC_LOCKED.val);
                oneReqQueue.setErrorInfo(CODE.ACC_LOCKED.mess + "|" + info);
                return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.ACC_LOCKED.getResult(tranId)).build();
            } else {
                if (accDao.getStatus() == Account.STATUS.LOCK.val) {
                    oneReqQueue.setCpCode(accDao.getCpCode());
                    oneReqQueue.setResult(CODE.ACC_LOCKED.val);
                    oneReqQueue.setErrorInfo(CODE.ACC_LOCKED.mess + "|" + info);
                    return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.ACC_LOCKED.getResult(tranId)).build();
                }
                oneReqQueue.setCpCode(accDao.getCpCode());
                if (!accDao.validIP(ip)) {
                    oneReqQueue.setResult(CODE.IP_SER_NOT_ALLOW.val);
                    oneReqQueue.setErrorInfo(CODE.IP_SER_NOT_ALLOW.mess + "|" + info);
                    return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.IP_SER_NOT_ALLOW.getResult(tranId)).build();
                }
                if (!validFunction(function)) {
                    oneReqQueue.setResult(CODE.UNKNOW_SERVICE.val);
                    oneReqQueue.setErrorInfo(CODE.UNKNOW_SERVICE.mess + "|" + info);
                    return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.UNKNOW_SERVICE.getResult(tranId)).build();
                }
                //-- Laybrand
                BrandLabel brand = BrandLabel.findFromCache(accDao.getUserName(), brandName);
                if (brand == null) {
                    oneReqQueue.setResult(CODE.BRAND_NOT_AVTIVE.val);
                    oneReqQueue.setErrorInfo(CODE.BRAND_NOT_AVTIVE.mess + "|" + info);
                    return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.BRAND_NOT_AVTIVE.getResult(tranId)).build();
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
                boolean operApproved = route.checkRole(operByPhone, type);
                if (!operApproved) {
                    oneReqQueue.setResult(CODE.SMS_TELCO_NOT_ALLOW.val);
                    oneReqQueue.setErrorInfo(CODE.SMS_TELCO_NOT_ALLOW.mess + "|" + info);

                    return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.SMS_TELCO_NOT_ALLOW.getResult(tranId)).build();
                }
                Tool.debug(DateProc.Timestamp2HHMMSS(1) + ": FROM [" + user + "] brand [" + brandName + "] to GW=" + oneReqQueue.getSendTo());
                //--
                oneReqQueue.setResult(CODE.RECEIVED.val);                       // Ket qua Trong Log income
                oneReqQueue.setErrorInfo(CODE.RECEIVED.mess);
                // DOI TUONG NAY BI THAY DOI NEU LOG INCOME 
                // CHUA XONG MA SUBMIT DA XONG THI BI UPDATE RESULT VA ERROR INFO
                // Clone ra De log cua gui va log income khong bi thay doi
                SmsBrandQueue queueSend = oneReqQueue.clone();
                AppStart.sendPrimaryTask.addToqueue(queueSend);
                // Return theo tai lieu la val.message
                return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.SUCCESS.getResult(tranId)).build();

            }
            // -----------
        } catch (Exception e) {
            oneReqQueue.setResult(CODE.EXCEPTION.val);
            oneReqQueue.setErrorInfo(CODE.EXCEPTION.mess + "|ExMessage=" + e.getMessage() + "|" + info);
            // Log Brand Message Income
            logger.error(Tool.getLogMessage(e));
            return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.ERROR.getResult(tranId)).build();
        } finally {
            delay = working.done();
            oneReqQueue.setProcessTime(delay);
            logDataInCome(oneReqQueue);
        }
    }

    //---------------
    @GET
    @Path("/{param}")
    public Response doGet(@PathParam("param") String path,
            @Context UriInfo uriInfo, @Context HttpServletRequest request) {
        try {
//            String ip = request.getRemoteAddr();
            String ip = Tool.getClientIpAddr(request);
            String host = request.getHeader("host");
            MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("function", path);
            properties.put("host", host);
            Set<String> key = queryParams.keySet();
            for (String name : key) {
                String value = queryParams.getFirst(name);
                properties.put(name, value);
            }
            return processRequest(properties, ip);
        } catch (Exception e) {
            return Response.status(Response.Status.OK.getStatusCode()).entity(CODE.EXCEPTION.getResult(e.hashCode() + "")).build();
        }
    }

    @POST
    @Path("/{param}")
    public Response doPost(@PathParam("param") String path,
            @Context UriInfo uriInfo, @Context HttpServletRequest request) {
        try {
            String ip = Tool.getClientIpAddr(request);
            String host = request.getHeader("host");
            MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("function", path);
            properties.put("host", host);
            Set<String> key = queryParams.keySet();
            for (String name : key) {
                String value = queryParams.getFirst(name);
                properties.put(name, value);
            }
            return processRequest(properties, ip);
        } catch (Exception e) {
            return Response.status(200).entity(CODE.EXCEPTION.getResult(e.hashCode() + "")).build();
        }
    }

    private boolean validFunction(String func) {
        boolean result = Boolean.FALSE;
        for (String onefunc : funtion) {
            if (onefunc.equalsIgnoreCase(func)) {
                result = Boolean.TRUE;
                break;
            }
        }
        return result;
    }

    private static void logDataInCome(SmsBrandQueue oneReqQueue) {
        AppStart.log_incomeTask.addToqueue(oneReqQueue);
        MyLog.logIncome(SmsQueueDao.toStringJson(oneReqQueue));
    }
}
