/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.db.DBPool;
import com.gk.htc.sendMT.service.ProcessSMS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Centurion
 */
public class MsgBrandCustomer {

    static final Logger logger = Logger.getLogger(MsgBrandCustomer.class);

    public ArrayList<MsgBrandCustomer> getAll_MsgCustomer() {
        ArrayList<MsgBrandCustomer> list = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM MSG_BRAND_CUSTOMER Where SCHEDULE_TIME <= NOW() ORDER BY REQUEST_TIME ASC";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();
            while (rs.next()) {
                MsgBrandCustomer one = new MsgBrandCustomer();
                one.setId(rs.getInt("ID"));
                one.setPhone(rs.getString("PHONE"));
                one.setOper(rs.getString("OPER"));
                one.setMessage(rs.getString("MESSAGE"));
                one.setLabel(rs.getString("LABEL"));
                one.setUserSender(rs.getString("USER_SENDER"));
                one.setTrandId(rs.getString("TRAND_ID"));
                one.setRequestTime(rs.getTimestamp("REQUEST_TIME"));
                one.setType(rs.getInt("TYPE"));
                one.setIsCampaign(rs.getInt("ISCAMPAIGN"));
                list.add(one);
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return list;
    }

    public boolean del(int id) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "DELETE  FROM MSG_BRAND_CUSTOMER WHERE ID = ?";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, id);
            result = pstm.executeUpdate() == 1;
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }

    private static void logDataInCome(SmsBrandQueue oneReqQueue) {
        AppStart.log_incomeTask.addToqueue(oneReqQueue);
        MyLog.logIncome(SmsQueueDao.toStringJson(oneReqQueue));
    }

    public SmsBrandQueue buildQueue() throws CloneNotSupportedException {
        SmsBrandQueue oneQueue = new SmsBrandQueue();
        oneQueue.setRequestTime(requestTime);
        phone = SMSUtils.PhoneTo84(phone);
        oneQueue.setSystemId(UniqueID.getId(phone));
        oneQueue.setPhone(phone);
        oneQueue.setLabel(label);
        oneQueue.setUserSender(userSender);
        oneQueue.setMessage(message);
        oneQueue.setType(type);
        if (Tool.checkNull(trandId)) {
            oneQueue.setTranId(userSender + "-" + id);
        } else {
            oneQueue.setTranId(trandId);
        }
        oneQueue.setOper(oper);
        oneQueue.setNode(MyConfig.LB_NODE);
        oneQueue.setSource(SmsBrandQueue.SOURCE.CMS.val);
        oneQueue.setStartTime(System.currentTimeMillis());
//        String info = "userSender:" + userSender + "|phone:" + phone + "|brandName:" + label + "|type:" + type;
//        info += "|mess:" + message + "|tranId:" + oneQueue.getTranId();
//        Tool.debug("One:MsgBrandCustomer:" + info);
        long delay = 0;
        long startTime = requestTime.getTime();
        BrandLabel brand = BrandLabel.findFromCache(userSender, label);
        if (brand == null) {
            delay = processTime(startTime);
            oneQueue.setProcessTime(delay);
            oneQueue.setResult(ProcessSMS.CODE.BRAND_NOT_AVTIVE.val);
            oneQueue.setErrorInfo(ProcessSMS.CODE.BRAND_NOT_AVTIVE.mess + "uuserSender:" + userSender);
            // Log Brand Message Income
            logDataInCome(oneQueue);
            return null;
        }
        RouteTable route = brand.getRoute();
        boolean operApproved = route.checkRole(oper, type);
        if (!operApproved) {
            delay = processTime(startTime);
            oneQueue.setProcessTime(delay);
            oneQueue.setResult(ProcessSMS.CODE.SMS_TELCO_NOT_ALLOW.val);
            oneQueue.setErrorInfo(ProcessSMS.CODE.SMS_TELCO_NOT_ALLOW.mess + "userSender:" + userSender);
            // Log Brand Message Income
            logDataInCome(oneQueue);
            return null;
        }
        // -- Dem tin long
        int totalMsg = 0;
        if (type == BrandLabel.TYPE.CSKH.val) {
            // Chu yeu chay thang nay
            totalMsg = SMSUtils.countSmsBrandCSKH(message, oper);
        }
        if (type == BrandLabel.TYPE.QC.val) {
            totalMsg = SMSUtils.countSmsBrandQC(message, oper);
        }
        if (totalMsg == SMSUtils.REJECT_MSG_LENG) {
            oneQueue.setTotalSms(totalMsg);
            delay = processTime(startTime);
            oneQueue.setProcessTime(delay);
            oneQueue.setResult(ProcessSMS.CODE.MSG_LENGTH_NOT_VALID.val);
            oneQueue.setErrorInfo(ProcessSMS.CODE.MSG_LENGTH_NOT_VALID.mess + "[Length=" + message.length() + "] =>" + "userSender:" + userSender);
            // Log Brand Message Income
            logDataInCome(oneQueue);
            return null;
        }
        oneQueue.setTotalSms(totalMsg);
        oneQueue.setUserSender(userSender);
        Account acc = Account.getAccount(userSender);
        if (acc != null) {
            oneQueue.setCpCode(acc.getCpCode());
        } else {
            oneQueue.setCpCode("UNKNOW_USER");
            oneQueue.setTotalSms(totalMsg);
            delay = processTime(startTime);
            oneQueue.setProcessTime(delay);
            oneQueue.setResult(ProcessSMS.CODE.UNKNOW_SERVICE.val);
            oneQueue.setErrorInfo(ProcessSMS.CODE.UNKNOW_SERVICE.mess + "[UNKNOW_USER] =>" + "userSender:" + userSender);
            // Log Brand Message Income
            logDataInCome(oneQueue);
            return null;
        }
        String group = route.getGroup(oper);
        oneQueue.setBrGroup(group);
        // Lay ra Huong Gui
        String _sendTo = route.getSendTo(oper, type);
        oneQueue.setSendTo(_sendTo);
        oneQueue.setResult(ProcessSMS.CODE.RECEIVED.val);
        oneQueue.setErrorInfo(ProcessSMS.CODE.RECEIVED.mess);
        // Log Brand Message Income
        logDataInCome(oneQueue.clone());
        //---
        if (isCampaign == 1) {
            oneQueue.setSarMsgRefNum(-1);
        }
        return oneQueue;

    }

    private long processTime(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }

    public String getUserSender() {
        return userSender;
    }

    public void setUserSender(String userSender) {
        this.userSender = userSender;
    }

    public String getTrandId() {
        return trandId;
    }

    public void setTrandId(String trandId) {
        this.trandId = trandId;
    }

    public int getIsCampaign() {
        return isCampaign;
    }

    public void setIsCampaign(int isCampaign) {
        this.isCampaign = isCampaign;
    }

    int id;
    String phone;
    String oper;
    String message;
    String label;
    String userSender;
    String trandId;
    Timestamp requestTime;
    int type;
    int isCampaign;
}
