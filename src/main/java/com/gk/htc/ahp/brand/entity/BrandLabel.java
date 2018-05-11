/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.db.DBPool;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Centurion
 */
public class BrandLabel implements Serializable {

    private static final long serialVersionUID = 7777984872572413382L;
    final static Logger logger = Logger.getLogger(BrandLabel.class);
    private static final HashMap<String, ArrayList<BrandLabel>> CACHE_ON_USER = new HashMap<>();
    private static final HashMap<String, BrandLabel> CACHE_ALL = new HashMap<>();

    static {
        // Lay ra tat Ca cac Brand
        ArrayList<BrandLabel> all = getAll();
        if (all != null && !all.isEmpty()) {
            // Duyet Qua Tung Brand
            for (BrandLabel oneBr : all) {
                String currentLabel = oneBr.getBrandLabel();
                CACHE_ALL.put(oneBr.getUserOwner() + "-" + currentLabel, oneBr);
                // Lay trong Cache ra list brand theo userOwner
                ArrayList<BrandLabel> tmp = CACHE_ON_USER.get(oneBr.getUserOwner());
                if (tmp == null) {
                    // Neu chua cho list thi tao moi list
                    tmp = new ArrayList<>();
                    // Add Brand nay vao list cuar userOwner
                    tmp.add(oneBr);
                    // Put vao Cache theo userOwner
                    CACHE_ON_USER.put(oneBr.getUserOwner(), tmp);
                } else {
                    // Neu co roi thi Add Brand nay vao list cuar userOwner cua no 
                    tmp.add(oneBr);
                    // Put vao Cache theo userOwner
                    CACHE_ON_USER.put(oneBr.getUserOwner(), tmp);
                }
            }
        }
    }

    public static BrandLabel getFromCache(int bid) {
        synchronized (CACHE_ALL) {
            BrandLabel result = null;
            Collection<BrandLabel> allVal = CACHE_ALL.values();
            for (BrandLabel oneVal : allVal) {
                if (oneVal.getId() == bid) {
                    result = oneVal;
                    break;
                }
            }
            CACHE_ALL.notifyAll();
            return result;
        }
    }

    public static BrandLabel findFromCache(int bid) {
        synchronized (CACHE_ALL) {
            BrandLabel result = null;
            Collection<ArrayList<BrandLabel>> allVal = CACHE_ON_USER.values();
            for (ArrayList<BrandLabel> oneVal : allVal) {
                for (BrandLabel oneLabel : oneVal) {
                    if (oneLabel.getId() == bid) {
                        result = oneLabel;
                        break;
                    }
                }
            }
            CACHE_ALL.notifyAll();
            return result;
        }
    }

    public static BrandLabel findFromCache(String userCp, String intputLabel) {
        synchronized (CACHE_ALL) {
            BrandLabel result = CACHE_ALL.get(userCp + "-" + intputLabel);
            return result;
        }
    }

    public static void showCache() {
        synchronized (CACHE_ALL) {
            HashMap<String, BrandLabel> currentTmp = (HashMap<String, BrandLabel>) CACHE_ALL.clone();
            CACHE_ALL.notifyAll();
            Set<String> keys = currentTmp.keySet();
            for (String one : keys) {
                MyLog.debug("debugKeyBrand:" + one);
            }
        }

    }

    public static void reload() {
        ArrayList<BrandLabel> all = getAll();
        synchronized (CACHE_ALL) {
//            HashMap<String, BrandLabel> currentTmp = (HashMap<String, BrandLabel>) CACHE_ALL.clone();
            CACHE_ALL.clear();
            if (all != null && !all.isEmpty()) {
                for (BrandLabel oneBr : all) {
                    String key = oneBr.getUserOwner() + "-" + oneBr.getBrandLabel();
                    CACHE_ALL.put(key, oneBr);
                }
            }
            CACHE_ALL.notifyAll();
        }
    }

    public static void reload(BrandLabel oneBr) {
        if (oneBr != null) {
            synchronized (CACHE_ALL) {
                String key = oneBr.getUserOwner() + "-" + oneBr.getBrandLabel();
                CACHE_ALL.put(key, oneBr);
                CACHE_ALL.notifyAll();
            }
        }
    }

    public static void reMove(BrandLabel br) {
        if (br != null) {
            synchronized (CACHE_ALL) {
                CACHE_ALL.remove(br.getUserOwner() + "-" + br.getBrandLabel());
                CACHE_ALL.notifyAll();
            }
        }
    }

    public static ArrayList<BrandLabel> getAll() {
        ArrayList<BrandLabel> all = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM BRAND_LABEL WHERE STATUS = 1 order by USER_OWNER";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();
            while (rs.next()) {
                BrandLabel one = new BrandLabel();
                one.setId(rs.getInt("ID"));
                one.setUserOwner(rs.getString("USER_OWNER"));
                one.setCp_code(rs.getString("CP_CODE"));
                one.setBrandLabel(rs.getString("BRAND_LABEL"));
                one.setCreateDate(rs.getTimestamp("CREATE_DATE"));
                one.setCreateBy(rs.getInt("CREATE_BY"));
                one.setStatus(rs.getInt("STATUS"));
                one.setFormTemplate(rs.getString("FORM_TEMPLATE"));
                one.setRoute(rs.getString("ROUTE_TABLE"));
                one.setPrice(rs.getInt("PRICE"));
                one.setPriority(rs.getInt("PRIORITY"));
                one.setOptionTelco(rs.getString("TELCO_OPTION"));
                all.add(one);
            }
        } catch (Exception e) {
            Tool.debug(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return all;
    }

    public BrandLabel getByRIdLabel(String _userOwner, String label) {
        BrandLabel result = null;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM BRAND_LABEL WHERE STATUS = 1 AND USER_OWNER = ? AND BRAND_LABEL = ?";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, _userOwner);
            pstm.setString(2, label);
            rs = pstm.executeQuery();
            if (rs.next()) {
                result = new BrandLabel();
                result.setId(rs.getInt("ID"));
                result.setUserOwner(rs.getString("USER_OWNER"));
                result.setCp_code(rs.getString("CP_CODE"));
                result.setBrandLabel(rs.getString("BRAND_LABEL"));
                result.setCreateDate(rs.getTimestamp("CREATE_DATE"));
                result.setCreateBy(rs.getInt("CREATE_BY"));
                result.setStatus(rs.getInt("STATUS"));
                result.setFormTemplate(rs.getString("FORM_TEMPLATE"));
                result.setRoute(rs.getString("ROUTE_TABLE"));
                result.setPrice(rs.getInt("PRICE"));
                result.setPriority(rs.getInt("PRIORITY"));
                result.setOptionTelco(rs.getString("TELCO_OPTION"));
            }
        } catch (Exception e) {
            Tool.debug(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }

    public BrandLabel getById(int id) {
        BrandLabel result = null;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM BRAND_LABEL WHERE  ID = ?";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, id);
            rs = pstm.executeQuery();
            if (rs.next()) {
                result = new BrandLabel();
                result.setId(rs.getInt("ID"));
                result.setUserOwner(rs.getString("USER_OWNER"));
                result.setCp_code(rs.getString("CP_CODE"));
                result.setBrandLabel(rs.getString("BRAND_LABEL"));
                result.setCreateDate(rs.getTimestamp("CREATE_DATE"));
                result.setCreateBy(rs.getInt("CREATE_BY"));
                result.setStatus(rs.getInt("STATUS"));
                result.setFormTemplate(rs.getString("FORM_TEMPLATE"));
                result.setRoute(rs.getString("ROUTE_TABLE"));
                result.setPrice(rs.getInt("PRICE"));
                result.setPriority(rs.getInt("PRIORITY"));
                result.setOptionTelco(rs.getString("TELCO_OPTION"));
            }
        } catch (Exception e) {
            Tool.debug(Tool.getLogMessage(e));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return result;
    }

    public static enum TYPE {

        CSKH(0, "Tin chăm sóc khách hàng"),
        QC(1, "Tin nhắn Quảng cáo"), //--
        ;

        public int val;
        public String desc;

        private TYPE(int val, String desc) {
            this.val = val;
            this.desc = desc;
        }

        public static String getDesc(int val) {
            String str = "Unknow";
            for (TYPE one : TYPE.values()) {
                if (one.val == val) {
                    str = one.desc;
                    break;
                }
            }
            return str;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(String userOwner) {
        this.userOwner = userOwner;
    }

    public String getCp_code() {
        return cp_code;
    }

    public void setCp_code(String cp_code) {
        this.cp_code = cp_code;
    }

    public String getBrandLabel() {
        return brandLabel;
    }

    public void setBrandLabel(String brandLabel) {
        this.brandLabel = brandLabel;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRouteByLabel() {
        return routeByLabel;
    }

    public void setRouteByLabel(int routeByLabel) {
        this.routeByLabel = routeByLabel;
    }

    public String getFormTemplate() {
        return formTemplate;
    }

    public void setFormTemplate(String formTemplate) {
        this.formTemplate = formTemplate;
    }

    public RouteTable getRoute() {
        return route;
    }

    public void setRoute(RouteTable route) {
        this.route = route;
    }

    public void setRoute(String strJson) {
        this.route = RouteTable.json2Object(strJson);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getOptionTelco() {
        return optionTelco;
    }

    public void setOptionTelco(String optionTelco) {
        this.optionTelco = optionTelco;
    }

    int id;
    String userOwner;
    String cp_code;
    String brandLabel;
    Timestamp createDate;
    int createBy;
    int status;
    int routeByLabel;
    String formTemplate;
    //
    RouteTable route;
    int price;
    int priority;
    String optionTelco;     // For VinaPhone etc..
}
