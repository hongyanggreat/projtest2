package com.gk.htc.ahp.brand.db;

import com.gk.htc.ahp.brand.common.Tool;
import java.sql.*;
import org.apache.log4j.Logger;
import snaq.db.ConnectionPool;

public class DBPool {

    static final Logger logger = Logger.getLogger(DBPool.class);
    //=============================Connection Pool------------------------------------------
    static final String DB_POOL_NAME = "brand";
    static ConnectionPool dbpool;
    private static final long timeOut = 5000;

    private static void CreatePool() {
        dbpool = PoolMng.getConnPool(DB_POOL_NAME);
        if (dbpool == null) {
            Tool.debug("=============> [" + DB_POOL_NAME + "] IS NULL ?????");
        }
    }

    static {
        CreatePool();
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = dbpool.getConnection(timeOut);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return conn;
    }

    public static void release() {
        dbpool.release();
    }

    public static void freeConn(ResultSet rs, PreparedStatement pstm, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
    }

    public static void releadRsPstm(ResultSet rs, PreparedStatement pstm) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstm != null) {
                pstm.close();
            }
        } catch (Exception e) {
        }
    }

    public static int size() {
        int size = dbpool.getSize();
        int checkout = dbpool.getCheckedOut();
        int free = dbpool.getFreeCount();
        int max = dbpool.getMaxPool();
        int min = dbpool.getMinPool();
        Tool.debug("PoolName:" + DB_POOL_NAME + "|size=" + size + "/checkout=" + checkout + "/free=" + free + "/max=" + max + "/min=" + min);
        return size;
    }
}
