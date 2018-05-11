package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.db.DBPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class Account {

    final Logger logger = Logger.getLogger(Account.class);
    public static final HashMap<String, Account> CACHE = new HashMap<>();

    static {
        Account dao = new Account();
        ArrayList<Account> cache = dao.getAgentcyAndUser();
        for (Account one : cache) {
            CACHE.put(one.getUserName(), one);
        }
    }

    public static void reload() {
        synchronized (CACHE) {
            Account dao = new Account();
            ArrayList<Account> allAcc = dao.getAgentcyAndUser();
            CACHE.clear();
            for (Account one : allAcc) {
                CACHE.put(one.getUserName(), one);
            }
            CACHE.notify();
        }

    }

    public static void reload(Account acc) {
        synchronized (CACHE) {
            if (acc != null) {
                CACHE.put(acc.getUserName(), acc);
            }
            CACHE.notify();
        }
    }

    public static void remove(Account acc) {
        synchronized (CACHE) {
            if (acc != null) {
                CACHE.remove(acc.getUserName());
            }
            CACHE.notify();
        }
    }

    public static Account getAccount(int id) {
        Account acc = null;
        synchronized (CACHE) {
            Collection<Account> values = CACHE.values();
            for (Account one : values) {
                if (one.getAccID() == id) {
                    acc = one;
                    break;
                }
            }
            CACHE.notify();
        }
        return acc;
    }

    public static Account getAccount(String user) {
        synchronized (CACHE) {
            return CACHE.get(user);
        }
    }

    public static int getIdByName(String name) {
        int id = 0;
        synchronized (CACHE) {
            Collection<Account> coll = CACHE.values();
            for (Account one : coll) {
                if (one.getUserName().equalsIgnoreCase(name)) {
                    id = one.getAccID();
                    break;
                }
            }
            CACHE.notify();
        }
        return id;
    }

    public static String getNameById(int id) {
        String name = "";
        synchronized (CACHE) {
            Collection<Account> coll = CACHE.values();
            for (Account one : coll) {
                if (one.getAccID() == id) {
                    name = one.getUserName();
                    break;
                }
            }
            CACHE.notify();
        }
        return name;
    }

    public static String geFulltNameById(int id) {
        Account one = getAccount(id);
        if (one != null) {
            return one.getFullName();
        } else {
            return "";
        }
    }

    /**
     * Bao Gom ca Tai Khoan Bi Khoa
     *
     * @return
     */
    private ArrayList<Account> getAgentcyAndUser() {
        ArrayList all = new ArrayList();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM ACCOUNTS WHERE USER_TYPE = ? OR USER_TYPE = ?";

        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            int i = 1;
            pstm.setInt(i++, TYPE.AGENCY.val);
            pstm.setInt(i++, TYPE.USER.val);

            rs = pstm.executeQuery();
            while (rs.next()) {
                Account acc = new Account();
                acc.setAccID(rs.getInt("ACC_ID"));
                acc.setParentId(rs.getInt("PARENT_ID"));
                acc.setUserName(rs.getString("USERNAME"));
                acc.setPassWord(rs.getString("PASSWORD"));
                acc.setFullName(rs.getString("FULL_NAME"));
                acc.setDescription(rs.getString("DESCRIPTION"));
                acc.setAddress(rs.getString("ADDRESS"));
                acc.setPhone(rs.getString("PHONE"));
                acc.setEmail(rs.getString("EMAIL"));
                acc.setMaxBrand(rs.getInt("MaxBrand"));
                acc.setCreateDate(rs.getTimestamp("CREATE_DATE"));
                acc.setCreateBy(rs.getString("CREATE_BY"));
                acc.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
                acc.setUpdateBy(rs.getString("UPDATE_BY"));
                acc.setUserType(rs.getInt("USER_TYPE"));
                acc.setStatus(rs.getInt("STATUS"));
                //
                acc.setIp_allow(rs.getString("IP_ALLOW"));
                acc.setPhone_Allow(rs.getString("PHONE_SEND"));
                acc.setPass_send(rs.getString("PASS_SEND"));
                acc.setCpCode(rs.getString("CP_CODE"));
                acc.setTps(rs.getInt("TPS"));
                acc.setAddressRange(rs.getString("ADDRESS_RANGE"));
                acc.setMethod(rs.getString("METHOD"));
                all.add(acc);
            }
        } catch (SQLException ex) {
            logger.error(Tool.getLogMessage(ex));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return all;
    }

    public Account getByUser(String user) {
        Account acc = null;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM ACCOUNTS WHERE USERNAME = ? ";
        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            int i = 1;
            pstm.setString(i++, user);
            //
            rs = pstm.executeQuery();
            if (rs.next()) {
                acc = new Account();
                acc.setAccID(rs.getInt("ACC_ID"));
                acc.setParentId(rs.getInt("PARENT_ID"));
                acc.setUserName(rs.getString("USERNAME"));
                acc.setPassWord(rs.getString("PASSWORD"));
                acc.setFullName(rs.getString("FULL_NAME"));
                acc.setDescription(rs.getString("DESCRIPTION"));
                acc.setAddress(rs.getString("ADDRESS"));
                acc.setPhone(rs.getString("PHONE"));
                acc.setEmail(rs.getString("EMAIL"));
                acc.setMaxBrand(rs.getInt("MaxBrand"));
                acc.setCreateDate(rs.getTimestamp("CREATE_DATE"));
                acc.setCreateBy(rs.getString("CREATE_BY"));
                acc.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
                acc.setUpdateBy(rs.getString("UPDATE_BY"));
                acc.setUserType(rs.getInt("USER_TYPE"));
                acc.setStatus(rs.getInt("STATUS"));
                //
                acc.setIp_allow(rs.getString("IP_ALLOW"));
                acc.setPhone_Allow(rs.getString("PHONE_SEND"));
                acc.setPass_send(rs.getString("PASS_SEND"));
                acc.setCpCode(rs.getString("CP_CODE"));
                acc.setTps(rs.getInt("TPS"));
                acc.setAddressRange(rs.getString("ADDRESS_RANGE"));
                acc.setMethod(rs.getString("METHOD"));
            }
        } catch (SQLException ex) {
            logger.error(Tool.getLogMessage(ex));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return acc;
    }

    public Account getById(int id) {
        Account acc = null;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM ACCOUNTS WHERE ACC_ID = ? ";

        try {
            conn = DBPool.getConnection();
            pstm = conn.prepareStatement(sql);
            int i = 1;
            pstm.setInt(i++, id);
            //
            rs = pstm.executeQuery();
            if (rs.next()) {
                acc = new Account();
                acc.setAccID(rs.getInt("ACC_ID"));
                acc.setParentId(rs.getInt("PARENT_ID"));
                acc.setUserName(rs.getString("USERNAME"));
                acc.setPassWord(rs.getString("PASSWORD"));
                acc.setFullName(rs.getString("FULL_NAME"));
                acc.setDescription(rs.getString("DESCRIPTION"));
                acc.setAddress(rs.getString("ADDRESS"));
                acc.setPhone(rs.getString("PHONE"));
                acc.setEmail(rs.getString("EMAIL"));
                acc.setMaxBrand(rs.getInt("MaxBrand"));
                acc.setCreateDate(rs.getTimestamp("CREATE_DATE"));
                acc.setCreateBy(rs.getString("CREATE_BY"));
                acc.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
                acc.setUpdateBy(rs.getString("UPDATE_BY"));
                acc.setUserType(rs.getInt("USER_TYPE"));
                acc.setStatus(rs.getInt("STATUS"));
                //
                acc.setIp_allow(rs.getString("IP_ALLOW"));
                acc.setPhone_Allow(rs.getString("PHONE_SEND"));
                acc.setPass_send(rs.getString("PASS_SEND"));
                acc.setCpCode(rs.getString("CP_CODE"));
                acc.setTps(rs.getInt("TPS"));
                acc.setAddressRange(rs.getString("ADDRESS_RANGE"));
                acc.setMethod(rs.getString("METHOD"));
            }
        } catch (SQLException ex) {
            logger.error(Tool.getLogMessage(ex));
        } finally {
            DBPool.freeConn(rs, pstm, conn);
        }
        return acc;
    }

    public Account checkLogin(String userName, String inputpassSend) {
        Account acc = null;
        try {
            acc = getAccount(userName);
            if (acc != null) {
                String dbPass_Send = acc.getPass_send();
//                Tool.debug("passSend from DB:" + myPassSend);
                if (Tool.checkNull(inputpassSend) || !dbPass_Send.equals(inputpassSend)) {
                    logger.warn("USER LOGIN FAIL Wrong PASS [u:" + userName + " - p:" + inputpassSend + "] tatus =" + acc.getStatus());
                    acc = null;
                }
//                else if (acc.getStatus() != STATUS.ACTIVE.val) {
//                    logger.warn("USER LOGIN FAIL Status inActive [u:" + userName + " - p:" + inputpassSend + "] tatus =" + acc.getStatus());
//                    acc = null;
//                }
            } else {
                Tool.debug("user from Cache is null");
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return acc;
    }

    //--
    private int accID;
    private int parentId;
    private String userName;
    private String passWord;
    private String fullName;
    private String description;
    private String address;
    private String phone;
    private String email;

    private int maxBrand;
    private Timestamp createDate;
    private String createBy;
    private Timestamp updateDate;
    private String updateBy;
    private int userType;
    private int status;
    //
    private String ip_allow;
    private String phone_Allow;
    String cpCode;
    private String pass_send;
    private int tps;
    private String addressRange;
    private String method;

    public static enum STATUS {

        ACTIVE(1),
        LOCK(0),
        DEL(404);
        public int val;

        private STATUS(int val) {
            this.val = val;
        }
    }

    public static enum TYPE {

        USER(0, "Người dùng"), // Create Ads - Manager allow Createby Id                  USER
        ADMIN(1, "Quyền quản trị"), // ADMIN
        AGENCY(2, "Đại lý"), // Duoc phep ket noi gui Qua API  // TODO co nen cho tao tk con hay khong ??
        AGENCY_MANAGER(3, "Quản lý Đại lý") // Chi co quyen quan ly thong ke, khong duoc lam gi ca cao hon quyen user la duoc thong ke nhieu dai ly
        ;
        public int val;
        public String name;

        private TYPE(int val, String name) {
            this.val = val;
            this.name = name;
        }
    }

    public static String getTypeName(int type) {
        String name = "Ko có quyền";
        if (type == TYPE.USER.val) {
            name = TYPE.USER.name;
        }
        if (type == TYPE.ADMIN.val) {
            name = TYPE.ADMIN.name;
        }
        return name;
    }

    public int getAccID() {
        return accID;
    }

    public void setAccID(int accID) {
        this.accID = accID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMaxBrand() {
        return maxBrand;
    }

    public void setMaxBrand(int maxBrand) {
        this.maxBrand = maxBrand;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getPass_send() {
        return pass_send;
    }

    public void setPass_send(String pass_send) {
        this.pass_send = pass_send;
    }

    public String getIp_allow() {
        return ip_allow;
    }

    public String getCpCode() {
        return cpCode;
    }

    public void setCpCode(String cpCode) {
        this.cpCode = cpCode;
    }

    public String[] getIpAllow() {
        if (!Tool.checkNull(ip_allow)) {
            return ip_allow.split("[, ]");
        } else {
            return null;
        }
    }

    public void setIp_allow(String ip_allow) {
        this.ip_allow = ip_allow;
    }

    public String getPhoneAllow() {
        return phone_Allow;
    }

    public void setPhone_Allow(String phone_Allow) {
        this.phone_Allow = phone_Allow;
    }

    public String[] getPhone_Allow() {
        if (!Tool.checkNull(phone_Allow)) {
            return phone_Allow.split("[, ]");
        } else {
            return null;
        }
    }

    public int getTps() {
        return tps;
    }

    public void setTps(int tps) {
        this.tps = tps;
    }

    public String getAddressRange() {
        return addressRange;
    }

    public void setAddressRange(String addressRange) {
        this.addressRange = addressRange;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean validIP(String ipRequest) {
        boolean flag = false;
        try {
            if (ipRequest.equals("127.0.0.1") && (userType == Account.TYPE.USER.val || userType == Account.TYPE.AGENCY.val)) {
                return true;
            }
            String[] ipAllow = getIpAllow();
            if (ipAllow != null) {
                for (String one : ipAllow) {
//                    Tool.debug("IP Request:[" + ipRequest + "]==>" + "IP Allow: [" + one + "]");
                    if (ipRequest.equals(one.trim())) {
                        flag = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return flag;
    }

    public boolean validPhone(String phoneRequest) {
        boolean flag = false;
        try {
            String[] phoneAllow = getPhone_Allow();
            if (phoneAllow != null) {
                for (String one : phoneAllow) {
//                    Tool.debug("phoneAllow:" + one + "]");
//                    Tool.debug("phoneRequest:" + phoneRequest + "]");
                    if (phoneRequest.equals(one)) {
                        flag = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return flag;
    }
}
