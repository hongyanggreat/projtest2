/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.common.SendMail;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.db.DBPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Cường <duongcuong96 at gmail dot com>
 */
public class AppConfig {

    static final Logger logger = Logger.getLogger(AppConfig.class);
    private static final Map<String, String> CACHE = new HashMap<>(); // chua data cua table config

    static {
        ArrayList<AppConfig> list = getAll();
        for (AppConfig one : list) {
            CACHE.put(one.getConfig_key(), one.getConfig_value());
        }
    }

    public static void reload() {
        ArrayList<AppConfig> list = getAll();
        CACHE.clear();
        for (AppConfig one : list) {
            CACHE.put(one.getConfig_key(), one.getConfig_value());
        }
    }

//    entity : 
    private Integer id;
    private String config_key;
    private String config_value;
    private String create_date;
    private String update_date;
    private String create_by;
    private String update_by;
    private String cf_desc;

    public static ArrayList<AppConfig> getAll() {
        ArrayList arr = new ArrayList();
        String sql = null;
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rs = null;

        try {
            sql = "SELECT * FROM CONFIG_DB";
            conn = DBPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                AppConfig tmp_app = new AppConfig();
                tmp_app.setID(rs.getInt("ID"));
                tmp_app.setConfig_key(rs.getString("CONFIG_KEY"));
                tmp_app.setCf_desc(rs.getString("CF_DESC"));
                tmp_app.setConfig_value(rs.getString("CONFIG_VALUE"));
                tmp_app.setCreate_by(rs.getString("CREATE_BY"));
                tmp_app.setUpdate_by(rs.getString("UPDATE_BY"));
                tmp_app.setCreate_date(rs.getString("CREATE_DATE"));
                tmp_app.setUpdate_date(rs.getString("UPDATE_DATE"));
                arr.add(tmp_app);
            }
        } catch (SQLException e) {
            Tool.debug("Error AppConfig.getAll() : ");
            logger.error(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstmt, conn);
        }

        return arr;
    }

    public static String getConfigValueCache(String key) {
        if (CACHE.get(key) != null) {
            return CACHE.get(key);
        } else {
            return "";
        }
    }

    public static int getConfigValueInt(String key) {
        if (CACHE.get(key) != null) {
            return Tool.getInt(CACHE.get(key));
        } else {
            return 0;
        }
    }

    public boolean sendAlertMail(String subject, String content) {
        String mailList = getConfigValueCache("ALERT_LIST_MAIL");
        int isSendMailOn = getConfigValueInt("CAN_SEND_MAIL");
        if (isSendMailOn >= 1) {
            return SendMail.sendMail(subject, content, mailList);
        } else {
            Tool.debug("---- Không gửi email cảnh báo vì CAN_SEND_MAIL không được đặt >= 1 ! ---- ");
            return false;
        }

    }

    public void setID(Integer ID) {
        this.id = ID;
    }

    public void setConfig_key(String config_key) {
        this.config_key = config_key;
    }

    public void setConfig_value(String config_value) {
        this.config_value = config_value;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }

    public void setCf_desc(String cf_desc) {
        this.cf_desc = cf_desc;
    }

    public Integer getID() {
        return id;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public String getCreate_date() {
        return create_date;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public String getConfig_key() {
        return config_key;
    }

    public String getConfig_value() {
        return config_value;
    }

    public String getCreate_by() {
        return create_by;
    }

    public String getUpdate_by() {
        return update_by;
    }

    public String getCf_desc() {
        return cf_desc;
    }

}
