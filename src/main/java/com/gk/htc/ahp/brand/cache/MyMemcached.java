/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.cache;

import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.Account;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.sendMT.service.ProcessSMS;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Private
 */
public class MyMemcached {

    private static final Logger logger = Logger.getLogger(MyMemcached.class);
    private static MyMemcached INSTANCE;
    private static final String MSG_PREFIX = "MSG";
    private static final String TRANSID_PREFIX = "TRANSID";
    private static final String TPS_PREFIX = "TPS";

    final private MemCachedClient MCC;

    static public MyMemcached getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MyMemcached();
        }
        return INSTANCE;
    }
    boolean isBINARY = true;
    boolean failover = false; // turn off auto-failover in event of server down
    /*
    trong khi lập trình truyền file bằng TCP thì nếu kích thước biến buffer lưu data nhỏ hơn MTU thì TCP không gởi gói tin đi liền mà
    đợi cho đủ MTU rồi mới gởi đi. Đó là cách làm việc của thuật toán Nagle với mục đích tránh quá nhiều gói tin có data kích thước 
    nhỏ truyền qua mạng.
     */
    boolean nagleAlg = false; // turn off Nagle's algorithm on all sockets in Pool
    boolean aliveCheck = false; // disable health check of socket on checkout
    // Khởi tạo và kết nối đến Memcached server

    private MyMemcached() {
        System.out.println("Init MyMemcached SockIOPool ...");
        System.out.println("MyConfig.MC_POOLNAME:" + MyConfig.MC_POOLNAME);
        System.out.println("MyConfig.MC_HOST:" + MyConfig.MC_HOST);
        System.out.println("MyConfig.MC_INIT_CONN:" + MyConfig.MC_INIT_CONN);
        System.out.println("MyConfig.MC_MIN_CONN:" + MyConfig.MC_MIN_CONN);
        System.out.println("MyConfig.MC_MAX_CONN:" + MyConfig.MC_MAX_CONN);
        System.out.println("MyConfig.MC_MAINT_SLEEP:" + MyConfig.MC_MAINT_SLEEP);
        System.out.println("MyConfig.MC_NAGLE:" + MyConfig.MC_NAGLE);
        System.out.println("MyConfig.MC_MAX_IDLE:" + MyConfig.MC_MAX_IDLE);
        System.out.println("MyConfig.MC_SOCKET_TO:" + MyConfig.MC_SOCKET_TO);
        System.out.println("MyConfig.MC_SOCKET_CONNECT_TO:" + MyConfig.MC_SOCKET_CONNECT_TO);

        SockIOPool pool = SockIOPool.getInstance(MyConfig.MC_POOLNAME);
        pool.setServers(MyConfig.MC_HOST.split(","));                               // String[] serverlist mullti Host For fail over
//        pool.setWeights(getWeightsProperty(MyConfig.MC_WEIGHTS));                   // 3,3  - số request tuần tự đến từng Server như Loadblancing
        pool.setInitConn(Tool.getInt(MyConfig.MC_INIT_CONN));                  // 50
        pool.setMinConn(Tool.getInt(MyConfig.MC_MIN_CONN));                    // 50
        pool.setMaxConn(Tool.getInt(MyConfig.MC_MAX_CONN));                    // 2014
        pool.setMaintSleep(Tool.getInt(MyConfig.MC_MAINT_SLEEP));              // 30
        pool.setNagle(Tool.getBoolean(MyConfig.MC_NAGLE));                     // FALSE
        pool.setMaxIdle(Tool.getInt(MyConfig.MC_MAX_IDLE));                    // 1000 * 60 * 30; // 30 minutes
        pool.setMaxBusyTime(1000 * 60 * 5);                                         // 1000 * 60 * 5
        pool.setMaintSleep(1000 * 5);                                               // 1000 * 5
        pool.setSocketTO(Tool.getInt(MyConfig.MC_SOCKET_TO));                  // 3000 => 3 seconds to block on reads
        pool.setSocketConnectTO(Tool.getInt(MyConfig.MC_SOCKET_CONNECT_TO));   // 0 3 seconds to block on initial
        // If you need to support multiple clients (i.e. Java, PHP, Perl, etc.)
        // you need to make a few changes when you are setting things up:
        pool.setHashingAlg(SockIOPool.NEW_COMPAT_HASH);
        pool.setAliveCheck(Tool.getBoolean(MyConfig.MC_SOCKET_TO));
        /*
            By default the java client will failover to a new server when a server
            dies.  It will also failback to the original if it detects that the
            server comes back (it checks the server in a falling off pattern).

            If you want to disable this (useful if you have flapping servers),
            there are two settings to handle this.
         */
//        pool.setFailover(failover);
//        pool.setFailback(false);
        pool.initialize();
        MCC = new MemCachedClient(MyConfig.MC_POOLNAME);
//        MCC = new MemCachedClient(MyConfig.MC_POOLNAME, isBINARY);
        MCC.setPrimitiveAsString(true);
    }

    //Hàm get một Object từ Memcached server thông qua key.
    private Object get(String key) {
        return MCC.get(key);
    }

    /**
     * Hàm add một Object vào Memcached server co Exprie Time
     *
     * @param key
     * @param exprie : Tính bằng mili Giây
     * @param value
     * @return
     */
    private boolean add(String key, Object value, long exprie) {
        return MCC.add(key, value, new Date(System.currentTimeMillis() + exprie));
    }

    //Tang Gia tri cua doi tuong co key len 1 va tra lai total sau khi tang
    private long incr(String key) {
        return MCC.incr(key);
    }

    //-----------------------
    public boolean checkDuplicateMsg(SmsBrandQueue oneReqQueue) {
        boolean result = Boolean.FALSE;
        try {
            String key_Msg = MSG_PREFIX + "." + oneReqQueue.getLabel() + "." + oneReqQueue.getPhone();
            String oldMessage = (String) get(key_Msg);
            if (!Tool.checkNull(oldMessage)) {
                // Co trong Cache
                return oldMessage.equals(oneReqQueue.getMessage());
            } else {
                add(key_Msg, oneReqQueue.getMessage(), ProcessSMS.EXPIRE_MSG);
            }
        } catch (Exception e) {
            logger.error("checkDuplicateMsg:" + Tool.getLogMessage(e));
        }
        return result;
    }

    /**
     * 5 Phút
     *
     * @param newQueue
     * @return
     */
    public boolean checkDuplicateTransId(SmsBrandQueue newQueue) {
        boolean result = Boolean.FALSE;
        try {
            String key_TransID = TRANSID_PREFIX + "-" + newQueue.getUserSender() + "-" + newQueue.getTranId();
            String oldTransID = (String) get(key_TransID);
            if (!Tool.checkNull(oldTransID)) {
                result = oldTransID.equals(newQueue.getTranId());
            } else {
                add(key_TransID, newQueue.getTranId(), ProcessSMS.EXPIRE_TRANSID);
            }
        } catch (Exception e) {
            logger.error("checkDuplicateTransId:" + Tool.getLogMessage(e));
        }
        return result;
    }

    public boolean overTPS(Account acc) {
        boolean result = Boolean.FALSE;

        try {
            String key = TPS_PREFIX + "-" + acc.getUserName();
            String tpsVal = (String) get(key);
            // TPS lưu trong cache: Current TPS
            int crTps = Tool.getInt(tpsVal, 0);
            int userTps = acc.getTps();
            if (crTps != 0) {
                if (userTps > 0 && userTps < crTps) {
                    // Nếu acc.getTps() =0 thi 0 < current Tps = 1 =? Reject
                    logger.error("overTPS: u=" + acc.getUserName() + ",tps=" + acc.getTps() + ",crTps =" + crTps + ",node=" + MyConfig.LB_NODE);
                    return true;    // tra ve Over TPS
                } else {
                    // Tang Tps len 1 don vi
                    incr(key);
                }
            } else {
                // Null Thi Them vao de dem
                long missTime = acc.getTps() * 2;
                add(key, 1, ProcessSMS.EXPIRE_TPS - missTime);
            }
        } catch (Exception e) {
            logger.error("Exception-overTPS:" + Tool.getLogMessage(e));
        }
        return result;
    }

    public void pushMemTest(String input) {
        add("tuantest", input, 60 * 1000);
    }

    public String getMemTest() {
        return (String) get("tuantest");
    }

    //Hàm set một Object vào Memcached server
//    private boolean set(String key, Object value) {
//        return MCC.set(key, value);
//
//    }
    /**
     * Hàm set một Object vào Memcached server co Exprie Time
     *
     * @param key
     * @param value
     * @param expireTime : Tính bằng Giây
     * @return
     */
//    private boolean set(String key, Object value, long expireTime) {
//        return MCC.set(key, value, new Date(System.currentTimeMillis() + expireTime * 1000));
//    }
    //Hàm get một Object từ Memcached server thông qua key.
//    private boolean delete(String key) {
//        return MCC.delete(key);
//    }
    //Hàm add một Object vào Memcached server
//    private boolean add(String key, Object value) {
//        return MCC.add(key, value);
//    }
//Tang Gia tri cua doi tuong co key thanh value+old_val va tra lai total sau khi tang
//    public long incr(String key, long value) {
//        return MCC.incr(key, value);
//    }
}
