/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.common.DoWork;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.db.DBPool;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class CDRSubmit implements Serializable {

    private static final long serialVersionUID = 7777994872572413382L;
    static final Logger logger = Logger.getLogger(CDRSubmit.class);

    public static void cdrProcess() {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT MAX(ID) FROM msg_brand_submit";
        int currentMaxId;
        int maxOld;
        DoWork work = new DoWork();
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();
            if (rs.next()) {
                // Lay ra Max ID cua Table msg_brand_submit
                currentMaxId = rs.getInt(1);
                System.out.println("Max ID msg_brand_submit: " + currentMaxId);
                // Giai Phong ResultSet
                DBPool.releadRsPstm(rs, pstm);
                sql = "SELECT MAX_ID FROM max_table_id WHERE TABLE_ID = 'msg_brand_submit'";
                pstm = conn.prepareStatement(sql);
                rs = pstm.executeQuery();
                if (rs.next()) {
                    maxOld = rs.getInt("MAX_ID");
                    System.out.println("Old Max ID msg_brand_submit: " + maxOld);
                    DBPool.releadRsPstm(rs, pstm);
                    sql = "UPDATE MAX_TABLE_ID SET MAX_ID = ?, NEXT_RUN = ? WHERE TABLE_ID = 'msg_brand_submit'";
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, currentMaxId);
                    pstm.setLong(2, (System.currentTimeMillis() + 5 * 60 * 1000));
                    if (pstm.executeLargeUpdate() == 1) {
                        // Cap nhat Max ID thanh cong bat dau dem
                        sql = "SELECT DATE_FORMAT(REQUEST_TIME,'%d/%m/%Y') AS LOG_DATE,SUM(TOTAL_SMS) AS TOTAL_COUNT,USER_SENDER,LABEL,"
                                + "TYPE,SEND_TO AS  GATEWAY,(case when (RESULT=1)  THEN  1  ELSE 0 END) AS RESULT,OPER,BR_GROUP,LB_NODE,CP_CODE "
                                + " FROM msg_brand_submit WHERE ID > ? AND ID <= ? GROUP BY LOG_DATE,USER_SENDER,LABEL,TYPE,SEND_TO,RESULT,OPER,BR_GROUP,LB_NODE,CP_CODE";
                        pstm = conn.prepareStatement(sql);
                        pstm.setInt(1, maxOld);
                        pstm.setInt(2, currentMaxId);
                        rs = pstm.executeQuery();
                        ArrayList<CDRSubmit> submitList = new ArrayList<>();
                        while (rs.next()) {
                            CDRSubmit oneSubmit = new CDRSubmit();
                            oneSubmit.setLogDate(rs.getString("LOG_DATE"));
                            oneSubmit.setTotalSms(rs.getInt("TOTAL_COUNT"));
                            oneSubmit.setUserSender(rs.getString("USER_SENDER"));
                            oneSubmit.setLabel(rs.getString("LABEL"));
                            oneSubmit.setType(rs.getString("TYPE"));
                            oneSubmit.setGateWay(rs.getString("GATEWAY"));
                            oneSubmit.setResult(rs.getInt("RESULT"));
                            oneSubmit.setOper(rs.getString("OPER"));
                            oneSubmit.setGroup(rs.getString("BR_GROUP"));
                            oneSubmit.setNode(rs.getString("LB_NODE"));
                            oneSubmit.setCpCode(rs.getString("CP_CODE"));
                            submitList.add(oneSubmit);
                        }
                        System.out.println("submitList Size:" + submitList.size());
                        DBPool.releadRsPstm(rs, pstm);
                        for (CDRSubmit one : submitList) {
                            // Duyet qua tung SubmitList
                            sql = "SELECT ID FROM CDR_SUBMIT WHERE DATE_FORMAT(LOG_DATE,'%d/%m/%Y') = DATE_FORMAT(?,'%d/%m/%Y') "
                                    + " AND USER_SENDER = ? AND BINARY LABEL = ? AND TYPE = ? AND GATEWAY = ? AND RESULT = ?"
                                    + " AND OPER = ? AND BR_GROUP = ? AND LB_NODE = ? AND CP_CODE = ?";
                            pstm = conn.prepareStatement(sql);
                            pstm.setString(1, one.getLogDate());
                            pstm.setString(2, one.getUserSender());
                            pstm.setString(3, one.getLabel());
                            pstm.setString(4, one.getType());
                            pstm.setString(5, one.getGateWay());
                            pstm.setInt(6, one.getResult());
                            pstm.setString(7, one.getOper());
                            pstm.setString(8, one.getGroup());
                            pstm.setString(9, one.getNode());
                            pstm.setString(10, one.getCpCode());
                            // Lay ra ID neu ton tai
                            rs = pstm.executeQuery();
                            if (rs.next()) {
                                int idUpdate = rs.getInt(1);
                                DBPool.releadRsPstm(rs, pstm);
                                sql = "UPDATE CDR_SUBMIT SET TOTAL_COUNT = TOTAL_COUNT + ? WHERE ID = ?";
                                pstm = conn.prepareStatement(sql);
                                pstm.setInt(1, one.getTotalSms());
                                pstm.setInt(2, idUpdate);
                                pstm.execute();
                            } else {
                                // Khong Next thi ko co va Insert New
                                DBPool.releadRsPstm(rs, pstm);
                                sql = "INSERT INTO CDR_SUBMIT(TOTAL_COUNT,LOG_DATE                    ,USER_SENDER,LABEL,TYPE,GATEWAY,RESULT,OPER,BR_GROUP,LB_NODE,CP_CODE) "
                                        + "            VALUES(    ?      ,STR_TO_DATE(?,'%d/%m/%Y')   ,     ?     ,  ?  ,  ? ,   ?   ,   ?  ,  ? ,   ?    ,   ?   ,   ?   )";
                                pstm = conn.prepareStatement(sql);
                                int k = 1;
                                pstm.setInt(k++, one.getTotalSms());
                                pstm.setString(k++, one.getLogDate());
                                pstm.setString(k++, one.getUserSender());
                                pstm.setString(k++, one.getLabel());
                                pstm.setString(k++, one.getType());
                                pstm.setString(k++, one.getGateWay());
                                pstm.setInt(k++, one.getResult());

                                pstm.setString(k++, one.getOper());
                                pstm.setString(k++, one.getGroup());
                                pstm.setString(k++, one.getNode());
                                pstm.setString(k++, one.getCpCode());
                                //--
                                pstm.execute();
                            }
                        }

                    }
                }
            }
            Tool.debug("Working CDR Process Done: " + work.done());
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
    }

    int id;
    String logDate;
    String userSender;
    String label;
    int totalSms;
    String type;
    String gateWay;
    int result;
    String oper;
    String group;
    String node;
    String cpCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getUserSender() {
        return userSender;
    }

    public void setUserSender(String userSender) {
        this.userSender = userSender;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getTotalSms() {
        return totalSms;
    }

    public void setTotalSms(int totalSms) {
        this.totalSms = totalSms;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGateWay() {
        return gateWay;
    }

    public void setGateWay(String gateWay) {
        this.gateWay = gateWay;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getCpCode() {
        return cpCode;
    }

    public void setCpCode(String cpCode) {
        this.cpCode = cpCode;
    }

}
