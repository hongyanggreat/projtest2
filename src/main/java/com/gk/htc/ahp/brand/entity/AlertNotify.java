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
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class AlertNotify {

    static Logger logger = Logger.getLogger(AlertNotify.class);
    public static ArrayList<AlertNotify> CACHE = new ArrayList<>();

    static {
        AlertNotify mdao = new AlertNotify();
        CACHE = mdao.getbyAll();
    }

    public static enum KIND {

        MONITOR(1, "Monitor he thong"),
        ALER(2, "Alert Error"), //--
        ALL(3, "Monitor - Alert"), //--
        ;

        public int val;
        public String desc;

        private KIND(int val, String desc) {
            this.val = val;
            this.desc = desc;
        }
    }

    public static enum TYPE {

        EMAIL(1, "Alert Email"),
        SMS(2, "Alert SMS"), //--
        ALL(3, "Alert Phone"), //--
        ;

        public int val;
        public String desc;

        private TYPE(int val, String desc) {
            this.val = val;
            this.desc = desc;
        }
    }

    public ArrayList<AlertNotify> getallMonitor() {
        ArrayList<AlertNotify> result = new ArrayList<>();
        for (AlertNotify one : CACHE) {
            if (one.getKind() == KIND.ALL.val || one.getKind() == KIND.MONITOR.val) {
                result.add(one);
            }
        }
        return result;
    }

    public ArrayList<AlertNotify> getallAler() {
        ArrayList<AlertNotify> result = new ArrayList<>();
        for (AlertNotify one : CACHE) {
            if (one.getKind() == KIND.ALL.val || one.getKind() == KIND.ALER.val) {
                result.add(one);
            }
        }
        return result;
    }

    public static void reload() {
        AlertNotify mdao = new AlertNotify();
        CACHE = mdao.getbyAll();
    }

    public void updateNotifyTime(int id) {
        if (delay == 0) {
            delay = 60;
        }
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "UPDATE MONITOR_APP SET LAST_NOTIFY = ?,NEXT_NOTIFY = ? WHERE ID = ?";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            int i = 1;
            long currentTime = System.currentTimeMillis();
            pstm.setLong(i++, currentTime);
            long nextTime = currentTime + (delay * 60 * 1000);
            pstm.setLong(i++, nextTime);
            pstm.setInt(i++, id);
            pstm.execute();
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
    }

    public ArrayList<AlertNotify> getbyAll() {
        ArrayList<AlertNotify> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM MONITOR_APP";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();
            while (rs.next()) {
                AlertNotify one = new AlertNotify();
                one.setNotifyId(rs.getInt("ID"));
                one.setPhone(rs.getString("RECEIVER_NUMBER"));
                one.setMessage(rs.getString("MESSAGE"));
                one.setLabelId(rs.getInt("LABEL_ID"));
                one.setKind(rs.getInt("KIND"));
                one.setType(rs.getInt("TYPE"));
                one.setContentEmail(rs.getString("CONTENT_EMAIL"));
                one.setStartTime(rs.getInt("START_TIME_ALERT"));
                one.setEndTime(rs.getInt("END_TIME_ALERT"));
                one.setEmail(rs.getString("EMAIL"));
                one.setLastNotify(rs.getLong("LAST_NOTIFY"));
                one.setNextNotify(rs.getLong("NEXT_NOTIFY"));
                one.setDelay(rs.getLong("DELAY_NOTIFY"));
                result.add(one);
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }
    //*******************
    private int notifyId;
    private String phone;
    private String message;
    private int labelId;
    private int kind;       // 1 Monitor 2: Alert 3: ALL
    private int type;       // 1: Alert Email 2: Phone 3 ALL
    private String contentEmail;
    private int startTime;
    private int endTime;
    private String email;
    //--
    private long lastNotify;
    private long nextNotify;
    private long delay;

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
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

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContentEmail() {
        return contentEmail;
    }

    public void setContentEmail(String contentEmail) {
        this.contentEmail = contentEmail;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLastNotify() {
        return lastNotify;
    }

    public void setLastNotify(long lastNotify) {
        this.lastNotify = lastNotify;
    }

    public long getNextNotify() {
        return nextNotify;
    }

    public void setNextNotify(long nextNotify) {
        this.nextNotify = nextNotify;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

}
