package com.gk.htc.ahp.brand.common;

import java.io.File;
import org.apache.log4j.BasicConfigurator;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.net.SMTPAppender;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jconfig.ConfigurationManagerException;
import org.jconfig.handler.XMLFileHandler;

public class MyConfig {

    static Logger logger = Logger.getLogger(MyConfig.class);
    private static final ConfigurationManager CFM = ConfigurationManager.getInstance();
    public static Configuration config;

    //------------
    public static String PATH_CACHE_LOG_MSG_BR_INCOME;
//    public static String PATH_CACHE_HISTORY_INCOME;
    public static String PATH_CACHE_LOG_MSG_BR_SUBMIT;
//    public static String PATH_CACHE_HISTORY_SUBMIT;
    public static String PATH_CACHE_BRAND_SEND;
    //-- Web Service
    public static String contextPath;
    public static int web_port;
    public static String LB_NODE = "";              // NODE Process
    public static boolean DE_BUG = false;
    public static boolean CONSOLE_OUT = false;
    public static boolean SV_ALERT = false;
    public static boolean NODE_DEV = false;
    public static boolean BUID_CDR = false;
    // SMPP Sever
    public static Integer SMPP_SV_PORT;
    public static Integer SMPP_SV_PROCESSOR_DEGREE;
    public static Integer SMPP_SV_MAX_WAIT_BIND;
    // SMPP SERVER VAS VIETTEL BULK
    public static String VAS_VT_IP;
    public static int VAS_VT_PORT;
    public static String VAS_VT_USER;
    public static String VAS_VT_PASS;
    //--VIETTEL BANK
    public static String SOUTH_IP;
    public static int SOUTH_PORT;
    public static String SOUTH_USER;
    public static String SOUTH_PASS;

    // Constant for memcached;
    static public String MC_HOST = "HOST";
    static public String MC_POOLNAME = "POOLNAME";
    static public String MC_WEIGHTS = "WEIGHTS";
    static public String MC_INIT_CONN = "INIT_CONN";
    static public String MC_MIN_CONN = "MIN_CONN";
    static public String MC_MAX_CONN = "MAX_CONN";
    static public String MC_MAINT_SLEEP = "MAINT_SLEEP";
    static public String MC_NAGLE = "NAGLE";
    static public String MC_MAX_IDLE = "MAX_IDLE";
    static public String MC_SOCKET_TO = "SOCKET_TO";
    static public String MC_SOCKET_CONNECT_TO = "SOCKET_CONNECT_TO";
    static public String MC_ALIVECHECK = "ALIVECHECK";
// -- MAIL CONFIG ---
    public static String SMTP_MAIL;
    public static String SMTP_PASS;
    public static String MAIL_DEBUG;
    public static String MAIL_HOST;
    SMTPAppender appender = new SMTPAppender();

    // Log Mail ------------->>
    public MyConfig() {
        try {
            appender.setTo("tuanpla@gmail.com");
            appender.setFrom("smpp@ahp.vn");
            appender.setSMTPHost("mail.mailbox.com");
            appender.setLocationInfo(true);
            appender.setSubject("Test Mail From Log4J");
            appender.setLayout(new PatternLayout());
            appender.activateOptions();
            logger.addAppender(appender);
            logger.error("Hello World");
        } catch (Exception e) {
            logger.error("Printing ERROR Statements", e);
        }
    }

    public static void loadConfig() {
        File file = new File("../config/config.xml");
        MyLog.debug(file.getName());
        XMLFileHandler handler = new XMLFileHandler();
        handler.setFile(file);
        try {
            MyLog.debug("trying to load file config");
            CFM.load(handler, "engineConfig");
            MyLog.debug("file config successfully processed");
            config = ConfigurationManager.getConfiguration("engineConfig");
            //-- Read MyConfig WebServer
            contextPath = config.getProperty("contextPath", "/", "webService");
            web_port = config.getIntProperty("port", 6688, "webService");
            //--
            SMTP_MAIL = getString("SMTP_MAIL", "alert@ahp.vn", "EMAIL");
            SMTP_PASS = getString("SMTP_PASS", "ahp@alert.vn", "EMAIL");
            MAIL_HOST = getString("MAIL_HOST", "mail.ahp.vn", "EMAIL");
            MAIL_DEBUG = getString("MAIL_DEBUG", "false", "EMAIL");
            //----------
            PATH_CACHE_LOG_MSG_BR_INCOME = MyConfig.getString("PATH_CACHE_LOG_MSG_BR_INCOME", "", "appconfig");
            PATH_CACHE_LOG_MSG_BR_SUBMIT = MyConfig.getString("PATH_CACHE_LOG_MSG_BR_SUBMIT", "", "appconfig");

            PATH_CACHE_BRAND_SEND = MyConfig.getString("PATH_CACHE_BRAND_SEND", "", "appconfig");
            //-- VAS BULK SMPP
            VAS_VT_IP = MyConfig.getString("VAS_VT_IP", "", "smppVasViettel");
            VAS_VT_PORT = MyConfig.getInt("VAS_VT_PORT", 9988, "smppVasViettel");
            VAS_VT_USER = MyConfig.getString("VAS_VT_USER", "", "smppVasViettel");
            VAS_VT_PASS = MyConfig.getString("VAS_VT_PASS", "", "smppVasViettel");
            //--South SMPP
            SOUTH_IP = MyConfig.getString("SOUTH_IP", "", "smpp_South");
            SOUTH_PORT = MyConfig.getInt("SOUTH_PORT", 9696, "smpp_South");
            SOUTH_USER = MyConfig.getString("SOUTH_USER", "", "smpp_South");
            SOUTH_PASS = MyConfig.getString("SOUTH_PASS", "", "smpp_South");
            // MemCached
            MC_HOST = MyConfig.getString(MC_HOST, "", "memcached");
            MC_POOLNAME = MyConfig.getString(MC_POOLNAME, "", "memcached");
            MC_WEIGHTS = MyConfig.getString(MC_WEIGHTS, "", "memcached");
            MC_INIT_CONN = MyConfig.getString(MC_INIT_CONN, "", "memcached");
            MC_MIN_CONN = MyConfig.getString(MC_MIN_CONN, "", "memcached");
            MC_MAX_CONN = MyConfig.getString(MC_MAX_CONN, "", "memcached");
            MC_MAINT_SLEEP = MyConfig.getString(MC_MAINT_SLEEP, "", "memcached");
            MC_NAGLE = MyConfig.getString(MC_NAGLE, "", "memcached");
            MC_MAX_IDLE = MyConfig.getString(MC_MAX_IDLE, "", "memcached");
            MC_SOCKET_TO = MyConfig.getString(MC_SOCKET_TO, "", "memcached");
            MC_SOCKET_CONNECT_TO = MyConfig.getString(MC_SOCKET_CONNECT_TO, "", "memcached");
            MC_ALIVECHECK = MyConfig.getString(MC_ALIVECHECK, "", "memcached");
            //--
            LB_NODE = MyConfig.getString("LB_NODE", "NOTEx", "appconfig");
            DE_BUG = MyConfig.getBoolean("DE_BUG", false, "appconfig");
            SV_ALERT = MyConfig.getBoolean("SV_ALERT", false, "appconfig");
            NODE_DEV = MyConfig.getBoolean("NODE_DEV", false, "appconfig");
            BUID_CDR = MyConfig.getBoolean("BUID_CDR", false, "appconfig");
            //-- SMPP SERVER
            SMPP_SV_PORT = getInt("SMPP_SV_PORT", 7777, "smppSever");
            SMPP_SV_PROCESSOR_DEGREE = getInt("SMPP_SV_PROCESSOR_DEGREE", 25, "smppSever");
            SMPP_SV_MAX_WAIT_BIND = getInt("SMPP_SV_MAX_WAIT_BIND", 10, "smppSever");
        } catch (ConfigurationManagerException e) {
            logger.error("can not load file config!");
            logger.error(Tool.getLogMessage(e));
            System.exit(0);
        }
    }

    public static void initLog4j() {
        String log4jPath = "../config/log4j.properties";
        //--
        File fileLog4j = new File(log4jPath);
        if (fileLog4j.exists()) {
            Tool.debug("====>Initializing Log4j:" + log4jPath);
            PropertyConfigurator.configure(log4jPath);
        } else {
            System.err.println("=====> *** " + log4jPath + " file not found, so initializing log4j with BasicConfigurator");
            BasicConfigurator.configure();
        }
    }

    //--------------------------------
    public static int getInt(String properties, int defaultVal, String categoryName) {
        try {
            return Integer.parseInt(config.getProperty(properties, defaultVal + "", categoryName));
        } catch (NumberFormatException e) {
            logger.error(Tool.getLogMessage(e));
            return defaultVal;
        }
    }

    public static boolean getBoolean(String properties, boolean defaultVal, String categoryName) {
        try {
            return Integer.parseInt(config.getProperty(properties, 1 + "", categoryName)) == 1;
        } catch (NumberFormatException e) {
            logger.error(Tool.getLogMessage(e));
            return defaultVal;
        }
    }

    public static long getLong(String properties, long defaultVal, String categoryName) {
        try {
            return Long.parseLong(config.getProperty(properties, defaultVal + "", categoryName));
        } catch (NumberFormatException e) {
            logger.error(Tool.getLogMessage(e));
            return defaultVal;
        }
    }

    public static Double getDouble(String properties, Double defaultVal, String categoryName) {
        try {
            return Double.parseDouble(config.getProperty(properties, defaultVal + "", categoryName));
        } catch (NumberFormatException e) {
            logger.error(Tool.getLogMessage(e));
            return defaultVal;
        }
    }

    public static String getString(String properties, String defaultVal, String categoryName) {
        try {
            String val = config.getProperty(properties, defaultVal, categoryName);
            MyLog.debug(properties + ": " + val);
            return val;
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            return defaultVal;
        }
    }

}
