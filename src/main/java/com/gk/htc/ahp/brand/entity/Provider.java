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
public class Provider {

    static final Logger logger = Logger.getLogger(Provider.class);
    public static ArrayList<Provider> CACHE = new ArrayList<>();

    static {
        CACHE = getALL();
    }

    public static int getPrividerID(String code) {
        int result = 0;
        for (Provider one : CACHE) {
            if (one.getCode().equalsIgnoreCase(code)) {
                result = one.getId();
            }
        }
        return result;
    }

    public static ArrayList<Provider> getALL() {
        ArrayList<Provider> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT A.* FROM PROVIDER A WHERE  STATUS =1  ORDER BY POS";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();
            while (rs.next()) {
                Provider one = new Provider();
                one.setId(rs.getInt("ID"));
                one.setPos(rs.getInt("POS"));
                one.setCode(rs.getString("CODE"));
                one.setName(rs.getString("NAME"));
                one.setClassSend(rs.getString("CLASS_SEND"));
                result.add(one);
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }

    private int id;
    private int pos;
    private String code;
    private String name;
    private String classSend;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassSend() {
        return classSend;
    }

    public void setClassSend(String classSend) {
        this.classSend = classSend;
    }

}
