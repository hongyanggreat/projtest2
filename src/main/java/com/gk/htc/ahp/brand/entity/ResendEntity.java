/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.db.DBPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class ResendEntity {

    static final Logger logger = Logger.getLogger(ResendEntity.class);

    public ArrayList<ResendEntity> getAllQueue(int max) {
        ArrayList<ResendEntity> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT ID,USER_SENDER,PHONE,LABEL,MESSAGE,REQUEST_TIME,SEND_BY,STATUS,INFO,TRAND_ID "
                + " FROM MSG_RESEND_BY_ADMIN WHERE STATUS = " + STATUS.READY.val + " LIMIT ?";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
//            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, max);
            rs = pstm.executeQuery();
            while (rs.next()) {
                try {
                    ResendEntity one = new ResendEntity();
                    one.setId(rs.getInt("ID"));
                    one.setUserSender(rs.getString("USER_SENDER"));
                    one.setPhone(rs.getString("PHONE"));
                    one.setLabel(rs.getString("LABEL"));
                    one.setMessage(rs.getString("MESSAGE"));
                    one.setRequestTime(rs.getTimestamp("REQUEST_TIME"));
                    one.setSendBy(rs.getString("SEND_BY"));
                    one.setStatus(rs.getInt("STATUS"));
                    one.setInfo(rs.getString("INFO"));
                    one.setTrandId(rs.getString("TRAND_ID"));
                    rs.deleteRow();
                    //--
                    result.add(one);
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                }
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }

    public void delProcessList(String transId) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "DELETE FROM MSG_RESEND_BY_ADMIN WHERE TRAND_ID = ?";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            int i = 1;
            pstm.setString(i++, transId);
            pstm.executeUpdate();
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
    }
//--
    int id;
    String userSender;
    String label;
    String phone;
    String message;
    Timestamp requestTime;
    String sendBy;
    int status;
    String info;
    String trandId;

    public String getTrandId() {
        return trandId;
    }

    public void setTrandId(String trandId) {
        this.trandId = trandId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserSender() {
        return userSender;
    }

    public void setUserSender(String userSender) {
        this.userSender = userSender;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static enum STATUS {

        WAIT(0, "Chờ duyệt"),
        READY(1, "Sẵn sàng gửi"),
        ERROR(504, "Lỗi");
        public int val;
        public String desc;

        private STATUS(int val, String desc) {
            this.val = val;
            this.desc = desc;
        }

        public static String getDesc(int val) {
            String str = "Unknow";
            for (STATUS one : STATUS.values()) {
                if (one.val == val) {
                    str = one.desc;
                    break;
                }
            }
            return str;
        }
    }
}
