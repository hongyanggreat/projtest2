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

/**
 *
 * @author TUANPLA
 */
public class Money_info {

    static final Logger logger = Logger.getLogger(Money_info.class);

    public static void insertLog(String amount, String info) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "INSERT INTO MONEY_VAS_VTE(AMOUNT,LAST_TIME,INFO) VALUES(?,NOW(),?)";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, amount);
            pstm.setString(2, info);
            pstm.executeUpdate();
        } catch (Exception e) {
//            e.printStackTrace();
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);

        }
    }
}
