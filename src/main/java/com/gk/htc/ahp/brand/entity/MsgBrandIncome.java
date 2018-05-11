/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.db.DBPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class MsgBrandIncome {

    private static final Logger logger = Logger.getLogger(MsgBrandIncome.class);

    public void logIncome(SmsBrandQueue oneQueue) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "INSERT INTO MSG_BRAND_INCOME(PHONE,OPER,MESSAGE,LABEL,TOTAL_SMS,USER_SENDER,REQUEST_TIME,   LOG_TIME       ,RESULT,TYPE,ERROR_INFO,TRANS_ID,SYS_ID,SEND_TO,BR_GROUP,PROCESSTIME,LB_NODE,CP_CODE,SOURCE)"
                + "                         VALUES(  ?  , ?  ,  ?    , ?   ,   ?     ,    ?     ,    ?       ,     NOW()        , ?    ,  ? ,     ?    ,   ?    ,  ?   ,   ?   ,   ?    ,    ?      ,   ?   ,   ?    ,   ?  )";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            //--
            int i = 1;
            pstm.setString(i++, SMSUtils.PhoneTo84(oneQueue.getPhone()));
            pstm.setString(i++, oneQueue.getOper());
            pstm.setString(i++, oneQueue.getMessage());
            pstm.setString(i++, oneQueue.getLabel());
            pstm.setInt(i++, oneQueue.getTotalSms());
            pstm.setString(i++, oneQueue.getUserSender());
            pstm.setTimestamp(i++, oneQueue.getRequestTime());
            // TODO Lao Qua
            pstm.setInt(i++, oneQueue.getResult());        // Chi la Success Receiver
            pstm.setInt(i++, oneQueue.getType());
            pstm.setString(i++, oneQueue.getErrorInfo());
            pstm.setString(i++, oneQueue.getTranId());
            pstm.setString(i++, oneQueue.getSystemId());
            //---
            pstm.setString(i++, oneQueue.getSendTo());
            pstm.setString(i++, oneQueue.getBrGroup());
            pstm.setLong(i++, oneQueue.getProcessTime());
            pstm.setString(i++, MyConfig.LB_NODE);
            pstm.setString(i++, oneQueue.getCpCode());
            pstm.setString(i++, oneQueue.getSource());
            //--
            pstm.execute();
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e) + "\n===>getPhone=" + oneQueue.getPhone()
                    + "|getOper=" + oneQueue.getOper()
                    + "|getLabel=" + oneQueue.getLabel()
                    + "|getTotalSms=" + oneQueue.getTotalSms()
                    + "|USER_SENDER=" + oneQueue.getUserSender()
                    + "|getTranId=" + oneQueue.getTranId()
                    + "|getSendTo=" + oneQueue.getSendTo()
                    + "|getBrGroup=" + oneQueue.getBrGroup()
                    + "|getCpCode=" + oneQueue.getCpCode()
            );
            logger.error(Tool.getLogMessage(e));
            oneQueue.setErrorInfo(e.getMessage());
            oneQueue.setCacheFrom(" logIncome: " + e.getMessage());
            SmsQueueDao.writeBrandQueue(oneQueue, MyConfig.PATH_CACHE_LOG_MSG_BR_INCOME, ".brLog");
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
    }

    public void removeInCome() {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "DELETE FROM MSG_BRAND_INCOME WHERE REQUEST_TIME <= (NOW() - INTERVAL '90' DAY)";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            int result = pstm.executeUpdate();
            MyLog.debug("MsgBrandIncome.removeInCome" + result);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
    }

    public void insertKPI_Income(String date) {
        System.out.println("--->Thuc thi insertKPI_Income: "+DateProc.createTimestamp());
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "INSERT INTO KPI_REQUEST(LOG_DATE,USER_SENDER,TOTAL_COUNT,RESULT,OPER,CP_CODE)"
                + " SELECT DATE_FORMAT(LOG_TIME,'%Y-%m-%d') AS LOG_TIME,USER_SENDER,SUM(TOTAL_SMS),RESULT,OPER,CP_CODE"
                + " from msg_brand_income " + buidPartition(date) + " WHERE DATE_FORMAT(LOG_TIME,'%d/%m/%Y') = ?"
                + " GROUP BY DATE_FORMAT(LOG_TIME,'%Y-%m-%d'),USER_SENDER, RESULT,OPER,CP_CODE ";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            int i = 1;
            pstm.setString(i++, date);
            pstm.execute();
            System.out.println("--->Finish insertKPI_Income: "+DateProc.createTimestamp());
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
    }
    
    private static String buidPartition(String date) {
        String result = "";
        if (!Tool.checkNull(date)) {
            try {
                String[] arr = date.split("/");
                if (arr.length == 3) {
                    if (arr[2].length() == 4) {
                        arr[2] = arr[2].substring(2);
                    }
                    result = "PARTITION(P_" + arr[1] + "_" + arr[2] + ")";
                }
            } catch (Exception e) {
                logger.error(Tool.getLogMessage(e));
            }
        }
        return result;
    }
}
