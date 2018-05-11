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
import org.apache.log4j.Logger;
import com.gk.htc.ahp.brand.jsmpp.session.SMPPServerSession;

/**
 *
 * @author tuanpla
 */
public class ClientInfo {

    private static final Logger logger = Logger.getLogger(ClientInfo.class);
    // 10 Client

    //**********
    private int clid;
    private String ip;
    private int port;
    private String clname;          // is SYSTEM ID
    private String addressRange;
    private String oper;
    private SMPPServerSession session;
    private int countError;
    private String sessionId;
    private int clPriority;
    private int tps;
    private long bindTime;

    public String getAddressRange() {
        return addressRange;
    }

    public void setAddressRange(String addressRange) {
        this.addressRange = addressRange;
    }

    public long getBindTime() {
        return bindTime;
    }

    public void setBindTime(long bindTime) {
        this.bindTime = bindTime;
    }

    public int checkExist() {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT ID FROM CLIENT WHERE UPPER(NAME) = UPPER(?) AND CLIENT_IP = ? AND ADDRESS_RANGE = ?";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            //--
            pstm.setString(1, clname);
            pstm.setString(2, ip);
            pstm.setString(3, addressRange);
            rs = pstm.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }

    public void updateClient(ClientInfo cl) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "UPDATE CLIENT SET CLIENT_IP = ?,PORT = ?,NAME = ?,SESSION_ID = ?,TIME_CONECT = NOW() WHERE ID = ?";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            int i = 1;
            pstm.setString(i++, cl.getIp());
            pstm.setInt(i++, cl.getPort());
            pstm.setString(i++, cl.getClname());
            pstm.setString(i++, cl.getSessionId());
            pstm.setInt(i++, cl.getClid());
            pstm.executeUpdate();
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
    }

    public boolean addClient(ClientInfo cl) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "INSERT INTO  CLIENT(CLIENT_IP,PORT,NAME,OPER,SESSION_ID,TIME_CONECT,ADDRESS_RANGE)"
                + "                VALUES(   ?     , ?  , ?  ,  ? ,    ?     ,  NOW()    ,     ?       )";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            //--
            int i = 1;
            pstm.setString(i++, cl.getIp());
            pstm.setInt(i++, cl.getPort());
            pstm.setString(i++, cl.getClname());
            pstm.setString(i++, cl.getOper());
            pstm.setString(i++, cl.getSessionId());
            pstm.setString(i++, cl.getAddressRange());
            result = pstm.executeUpdate() == 1;
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }

    public static boolean delClient(String name) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "DELETE FROM CLIENT WHERE upper(NAME) = upper(?)";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            //--
            pstm.setString(1, name);
            result = pstm.executeUpdate() == 1;
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }

    public static boolean clearClient() {
        boolean result = false;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "DELETE FROM CLIENT";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            //--
            result = pstm.executeUpdate() == 1;
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }

    public static boolean delbySessionId(String sessionId) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "DELETE FROM CLIENT WHERE upper(SESSION_ID) = upper(?)";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            //--
            pstm.setString(1, sessionId);
            result = pstm.executeUpdate() == 1;
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }

    public int getClid() {
        return clid;
    }

    public void setClid(int clid) {
        this.clid = clid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClname() {
        return clname;
    }

    public void setClname(String clname) {
        this.clname = clname;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public SMPPServerSession getSession() {
        return session;
    }

    public void setSession(SMPPServerSession session) {
        this.session = session;
    }

    public int getCountError() {
        return countError;
    }

    public void setCountError() {
        this.countError = countError++;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getClPriority() {
        return clPriority;
    }

    public void setClPriority(int clPriority) {
        this.clPriority = clPriority;
    }

    public int getTps() {
        return tps;
    }

    public void setTps(int tps) {
        this.tps = tps;
    }
}
