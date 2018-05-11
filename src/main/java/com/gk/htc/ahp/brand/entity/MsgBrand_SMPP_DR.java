/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.db.DBPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class MsgBrand_SMPP_DR {

    private static final Logger logger = Logger.getLogger(MsgBrand_SMPP_DR.class);

    public void logDRResult(MsgBrand_SMPP_DR msg) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "INSERT INTO MSG_BRAND_SMPP_DR( PHONE,OPER,MESSAGE,REQUEST_TIME,    LOG_TIME      ,TRANS_ID,SEND_TO,LB_NODE,SUBMITTED,DLVRD,STATUS,ERROR,MESSAGEID)"
                + "                          VALUES(   ?  , ?  ,  ?    ,    ?       ,      NOW()       ,   ?    ,   ?   ,   ?   ,    ?    , ?   ,   ?  ,  ?  ,    ?    )";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
//            //--
            int i = 1;
            pstm.setString(i++, SMSUtils.PhoneTo84(msg.getPhone()));
            pstm.setString(i++, SMSUtils.buildMobileOperator(msg.getPhone()));
            pstm.setString(i++, msg.getMessage());
            pstm.setTimestamp(i++, msg.getRequestTime());
            pstm.setString(i++, msg.getTranId());
            pstm.setString(i++, msg.getSendTo());
            pstm.setString(i++, MyConfig.LB_NODE);
            pstm.setString(i++, msg.getSubmitted());
            pstm.setString(i++, msg.getDlvrd());
            pstm.setString(i++, msg.getStatus());
            pstm.setString(i++, msg.getError());
            pstm.setString(i++, msg.getMessageId());
            //--
            pstm.execute();
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
    }
    int id;
    String phone;
    String oper;
    String message;
    Timestamp requestTime;
    String tranId;          // ID cua KH gui den AHP
    String sendTo;
    //-- For Add Queue
    boolean shutDown;
    String lbNode;

    String submitted;
    String dlvrd;
    String status;
    String error;
    String messageId;       // MessageId cua SMPP gui ve khi ket noi voi nha mạng

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

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public boolean isShutDown() {
        return shutDown;
    }

    public void setShutDown(boolean shutDown) {
        this.shutDown = shutDown;
    }

    public String getLbNode() {
        return lbNode;
    }

    public void setLbNode(String lbNode) {
        this.lbNode = lbNode;
    }

    public String getSubmitted() {
        return submitted;
    }

    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }

    public String getDlvrd() {
        return dlvrd;
    }

    public void setDlvrd(String dlvrd) {
        this.dlvrd = dlvrd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

}
