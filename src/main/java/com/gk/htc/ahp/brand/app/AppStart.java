package com.gk.htc.ahp.brand.app;

import com.gk.htc.ahp.brand.cache.MyMemcached;
import com.gk.htc.ahp.brand.service.primarywork.WorkQueue;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.db.PoolMng;
import com.gk.htc.ahp.brand.thread.AlertNotify_task;
import com.gk.htc.ahp.brand.thread.ConcatLongMT;
import com.gk.htc.ahp.brand.thread.LogMsgBrandIncome;
import com.gk.htc.ahp.brand.thread.LogMsgBrandSubmit;
import com.gk.htc.ahp.brand.thread.MoniterApp;
import com.gk.htc.ahp.brand.thread.ReSend_Import;
import com.gk.htc.ahp.brand.thread.ReloadAble;
import com.gk.htc.ahp.brand.thread.SendBrandALL;
import com.gk.htc.ahp.brand.thread.SendBrandCustomer;
import com.gk.htc.sendMT.service.ServiceMapping;
import com.gk.htc.sendMT.service.thread.ANTIT_Task;
import com.gk.htc.sendMT.service.thread.CMC_Task;
import com.gk.htc.sendMT.service.thread.CMC_ViaMFS_Task;
import com.gk.htc.sendMT.service.thread.MBF_ViaNetViet_Task;
import com.gk.htc.sendMT.service.thread.VasVTE_ViaMFS_Task;
import com.gk.htc.sendMT.service.thread.MOBI_Service_Task;
import com.gk.htc.sendMT.service.thread.SOUTH_Task;
import com.gk.htc.sendMT.service.thread.VIHAT_Task;
import com.gk.htc.sendMT.service.thread.VMG_Task;
import com.gk.htc.sendMT.service.thread.VNPTMedia_Task;
import com.gk.htc.sendMT.service.thread.VasVTE_HTC_Task;
import com.gk.htc.sendMT.service.thread.VIVAS_Task;
import com.gk.htc.sendMT.service.thread.ZoneSms_Task;
import com.gk.htc.sendMT.service.thread.HNK_Task;
import com.gk.htc.sendMT.service.thread.HuongDEV_Taks;
import com.gk.htc.sendMT.service.thread.MFS_ANTIT_Task;
import com.gk.htc.sendMT.service.thread.MS_VIA_MFS_Task;
import com.gk.htc.sendMT.service.thread.MBS_Verify_VIA_MFS_Task;
import com.gk.htc.sendMT.service.thread.SOUTH_ViaMFS_Task;
import com.gk.htc.sendMT.service.thread.Send2FTS_Proxy_Task;
import com.gk.htc.sendMT.service.thread.SouthSMPP_Task;
import com.gk.htc.sendMT.service.thread.VNPT_HNviaMFS_Task;
import com.gk.htc.sendMT.service.thread.VNPTMediaUnicode_Task;
import com.gk.htc.sendMT.service.thread.VNPT_ND_Task;
import com.gk.htc.sendMT.service.thread.VTEHni_ViaAD_Task;
import com.gk.htc.sendMT.service.thread.VasVTE_ViaHTC_Task;
import org.apache.log4j.Logger;

/**
 * *
 * Lop Main Start xu ly
 *
 * @author PLATUAN
 */
public class AppStart {

    private static final Logger logger = Logger.getLogger(AppStart.class);
    //--
    public static boolean isRuning = true;
    //--
    private static int maxThread;
    public static WorkQueue workQueue = null;
    public static int TPS_LOG = 200;
    public static MyMemcached memcache;

    static {
        try {
            // Log4j
            MyConfig.initLog4j();
            // Load Config
            MyConfig.loadConfig();
            // -- 
            maxThread = MyConfig.getInt("maxThread", 30, "appconfig");
            // Khoi Tao Cache
            memcache = MyMemcached.getInstance();
            TPS_LOG = MyConfig.getInt("TPS_LOG", 100, "appconfig");
            //***********KHOI TAO ConnectionPoolManager**************
            if (!PoolMng.CreatePool()) {
                Tool.debug("Khong khoi tao duoc ket noi DB");
                System.exit(1);
            }
            ServiceMapping.loadService();
            //-----------------------------------------
        } catch (Exception ex) {
            logger.error("Thong so gateway chua du..." + Tool.getLogMessage(ex));
            System.exit(1);
        }
    }
    //--------------------------------

    // SMPP Luong Thuong
//    public static SmppClient smppcl_Bulk;
    // SMPP TMDT Server IDC VTE cua HTC
//    public static SmppClient smppcl_tmdt;
    /**
     * Thread Process*
     */
    public static ConcatLongMT concatLong_Task;
    public static LogMsgBrandIncome log_incomeTask;
    public static LogMsgBrandSubmit log_submitTask;
    public static SendBrandALL sendPrimaryTask;
    private static AlertNotify_task notify;
    private static ReloadAble reload;
    private static ReSend_Import reSendErr;
    private static SendBrandCustomer sendCus_Msg;
    // private static SmppSever smppServer;
    public static WebServer websever;
    //************ SEND 2 PROVIDER THREAD ***************
    public static HuongDEV_Taks dev_task;
    public static ANTIT_Task antit_task;
    public static MFS_ANTIT_Task mfs_antit_task;
    public static CMC_Task cmc_task;
    public static CMC_ViaMFS_Task cmcViaMFS_task;           // Gui qua tài khoản Cấp cho Mbifone Service
    public static HNK_Task hnk_task;
    public static MBF_ViaNetViet_Task mbfViaNetViet_task;           // Huong Mobifone qua Tai Khoan NetViet
    public static MOBI_Service_Task mfs_task;
    // public static Send2SMPPVasVTE_Task send_vte_Bulk;
    public static SOUTH_ViaMFS_Task southMFS_task;         // Gưi Qua tài khoản adt của Mobifone Service
    public static Send2FTS_Proxy_Task FTS_Proxy_Task;         // Gưi Qua tài khoản adt của HTC FTS_Proxy_Task
    public static SOUTH_Task south_task;
    public static SouthSMPP_Task south_smpp_task;
    public static VIHAT_Task vihat_task;
    public static VIVAS_Task vivas_task;
    public static VMG_Task vmg_task;
    public static VNPTMedia_Task vnptMedia_task;
    public static VNPTMediaUnicode_Task vnptMediaUnicode_task; // gửi tin nhan unicode
//    public static Send2_VNPTNET_Task vnpt_net;
    public static VNPT_ND_Task vnpt_nd;
    public static VNPT_HNviaMFS_Task vnpt_hn_mfs_task;
    public static MS_VIA_MFS_Task MSviaMFS_Task; // 
    public static MBS_Verify_VIA_MFS_Task MBS_Verify_VIA_MFS_Task; // 
    public static VTEHni_ViaAD_Task VteHni_AD;              // Gui qua Viettel HNI qua Anh Duc
    public static VasVTE_HTC_Task vasVTE_HTC;
    public static VasVTE_ViaHTC_Task vasVTE_viaHTC;       // Huong VAS cua Mobifone Service
    public static VasVTE_ViaMFS_Task vasVTE_MFS;       // Huong VAS cua Mobifone Service
    public static ZoneSms_Task zoneSms_task;  // SMS 10 so

    public static SmppClient south_client;
    
    //Dat bien toan cuc de check so tin chia kenh cho huong vte-mfs
    public static int numberChannel = 0;

    public static void shutDownSmpp() {
        if (south_client != null) {
            south_client.shutdown();  // South Client
        }
    }

    //--
    public static void appStop() {
        if (websever != null) {
            websever.stop();
        }
//        if (smppServer != null) {
//            smppServer.shutDown();
//        }
        //--
//        if (concatLong_Task != null) {
//            concatLong_Task.shutDown();
//        }
        if (log_incomeTask != null) {
            log_incomeTask.shutDown();
        }

        if (log_submitTask != null) {
            log_submitTask.shutDown();
        }

        if (sendPrimaryTask != null) {
            sendPrimaryTask.shutDown();
        }

        if (MyConfig.SV_ALERT) {
            if (notify != null) {
                notify.shutDown();
            }
            if (reSendErr != null) {
                reSendErr.shutDown();
            }
            if (sendCus_Msg != null) {
                sendCus_Msg.shutDown();
            }
        }
        if (reload != null) {
            reload.shutDown();
        }
        //====>>>>> Send Provider
        if (dev_task != null) {
            dev_task.shutDown();
        }
        if (antit_task != null) {
            antit_task.shutDown();
        }
        if (mfs_antit_task != null) {
            mfs_antit_task.shutDown();
        }
        if (cmc_task != null) {
            cmc_task.shutDown();
        }
        if (cmcViaMFS_task != null) {
            cmcViaMFS_task.shutDown();
        }
        if (hnk_task != null) {
            hnk_task.shutDown();
        }
        if (mbfViaNetViet_task != null) {
            mbfViaNetViet_task.shutDown();
        }
        if (mfs_task != null) {
            mfs_task.shutDown();
        }
        //        if (send_vte_Bulk != null) {
//            send_vte_Bulk.shutDown();
//        }
        if (southMFS_task != null) {
            southMFS_task.shutDown();
        }
        if (FTS_Proxy_Task != null) {
            FTS_Proxy_Task.shutDown();
        }
        if (south_task != null) {
            south_task.shutDown();
        }
        if (south_smpp_task != null) {
            south_smpp_task.shutDown();
        }
        if (vihat_task != null) {
            vihat_task.shutDown();
        }
        if (vivas_task != null) {
            vivas_task.shutDown();
        }
        if (vmg_task != null) {
            vmg_task.shutDown();
        }
        if (vnptMedia_task != null) {
            vnptMedia_task.shutDown();
        }
        if (vnptMediaUnicode_task != null) {
            vnptMediaUnicode_task.shutDown();
        }
//        if (vnpt_net != null) {
//            vnpt_net.shutDown();
//        }
        if (vnpt_nd != null) {
            vnpt_nd.shutDown();
        }
        if (MSviaMFS_Task != null) {
            MSviaMFS_Task.shutDown();
        }
        if (MBS_Verify_VIA_MFS_Task != null) {
            MBS_Verify_VIA_MFS_Task.shutDown();
        }
        
        if (vnpt_hn_mfs_task != null) {
            vnpt_hn_mfs_task.shutDown();
        }
        if (VteHni_AD != null) {
            VteHni_AD.shutDown();
        }
        if (vasVTE_HTC != null) {
            vasVTE_HTC.shutDown();
        }
        if (vasVTE_viaHTC != null) {
            vasVTE_viaHTC.shutDown();
        }
        if (vasVTE_MFS != null) {
            vasVTE_MFS.shutDown();
        }
        if (zoneSms_task != null) {
            zoneSms_task.shutDown();
        }
    }

    public static void main(String[] args) {
        try {
            workQueue = new WorkQueue("WorkQueue Main", maxThread);
            //-- LogMsgBrandIncome
            if (!MyConfig.NODE_DEV) {
                log_incomeTask = new LogMsgBrandIncome();    // INCOME
                log_incomeTask.start();
                //-- LogMsgBrandSubmit
                log_submitTask = new LogMsgBrandSubmit();    // SUBMIT
                log_submitTask.start();
                // Send Brand CSKH
                sendPrimaryTask = new SendBrandALL(workQueue);
                sendPrimaryTask.start();
                // RELOAD
                reload = new ReloadAble();
                reload.start();
                /**
                 * *****SEND BRAND THREAD********
                 */
                // SMPP Client SOUTH
                south_client = new SmppClient(
                        MyConfig.SOUTH_IP,
                        MyConfig.SOUTH_PORT,
                        MyConfig.SOUTH_USER,
                        MyConfig.SOUTH_PASS,
                        "SOUTH_SMPP");
                south_client.start();
                // SMPP Client BULK
//            smppcl_Bulk = new SmppClient(
//                    MyConfig.VAS_VT_IP,
//                    MyConfig.VAS_VT_PORT,
//                    MyConfig.VAS_VT_USER,
//                    MyConfig.VAS_VT_PASS,
//                    "VAS_PUB");
//            smppcl_Bulk.start();

//            concatLong_Task = new ConcatLongMT();
//            concatLong_Task.start();
                //1-- Send2 ANTIT
                antit_task = new ANTIT_Task();
                antit_task.start();
                //2-- Send2 ANTIT
                cmc_task = new CMC_Task();
                cmc_task.start();
                //--
                cmcViaMFS_task = new CMC_ViaMFS_Task();
                cmcViaMFS_task.start();
                // 3-- Send2 HNK
                hnk_task = new HNK_Task();
                hnk_task.start();
                //--
                mbfViaNetViet_task = new MBF_ViaNetViet_Task();
                mbfViaNetViet_task.start();
                // 4-- Mobi Service
                mfs_task = new MOBI_Service_Task();
                mfs_task.start();
               
                // 5-- Send2 SMPP_VasVTE BULK
                south_task = new SOUTH_Task();
                south_task.start();
                // 6-- Send2 SMPP_VasVTE BULK
                south_smpp_task = new SouthSMPP_Task();
                south_smpp_task.start();
                // 7-- Send2 VIHAT
                vihat_task = new VIHAT_Task();
                vihat_task.start();
                // 8-- Send2 VIVAS
                vivas_task = new VIVAS_Task();
                vivas_task.start();
                // 9-- Send2 VMG
                vmg_task = new VMG_Task();
                vmg_task.start();
                // 10-- Send2 VNPT MEDIA CHAY CHINH
                vnptMedia_task = new VNPTMedia_Task();
                vnptMedia_task.start();

                // 10-- Send2 VNPT MEDIA UNICODE
                vnptMediaUnicode_task = new VNPTMediaUnicode_Task();
                vnptMediaUnicode_task.start();
                
                //===========START DUONGNH
                
                 // 5-- Send2 SMPP_VasVTE BULK
                southMFS_task = new SOUTH_ViaMFS_Task();
                southMFS_task.start();
               
                // 5--  Send2FTS_Proxy_Task
                FTS_Proxy_Task = new Send2FTS_Proxy_Task();
                FTS_Proxy_Task.start();
                
                
                //1-- Send2 ANTIT
                mfs_antit_task = new MFS_ANTIT_Task();
                mfs_antit_task.start();
                //===========END DUONGNH
                /// VNPT NET
                //--VNPT Nam Dinh
                vnpt_nd = new VNPT_ND_Task();
                vnpt_nd.start();
                //--VNPT Nam Dinh

                //MS via MFS
                MSviaMFS_Task = new MS_VIA_MFS_Task();
                MSviaMFS_Task.start();
                //MBS VERIFY 3 via MFS
                MBS_Verify_VIA_MFS_Task = new MBS_Verify_VIA_MFS_Task();
                MBS_Verify_VIA_MFS_Task.start();
                //VNPT HN via MFS
                vnpt_hn_mfs_task = new VNPT_HNviaMFS_Task();
                vnpt_hn_mfs_task.start();

                //MS via MFS
                VteHni_AD = new VTEHni_ViaAD_Task();
                VteHni_AD.start();
                // 14-- Send2 VAS VIETTEL WS
                vasVTE_HTC = new VasVTE_HTC_Task();
                vasVTE_HTC.start();
                // 14-- Send2 VAS VIETTEL WS
                vasVTE_viaHTC = new VasVTE_ViaHTC_Task();
                vasVTE_viaHTC.start();
                //--
                vasVTE_MFS = new VasVTE_ViaMFS_Task();
                vasVTE_MFS.start();
                
                // 15-- Send2 VIVAS
                zoneSms_task = new ZoneSms_Task();
                zoneSms_task.start();
            }
            dev_task = new HuongDEV_Taks();
            dev_task.start();
            // AlertNotify
            if (MyConfig.SV_ALERT) {
                notify = new AlertNotify_task();
                notify.start();
                // Gui lai tin nhan bi loi do he thong CHI MASTER moi chay
                reSendErr = new ReSend_Import();
                reSendErr.start();
                sendCus_Msg = new SendBrandCustomer();
                sendCus_Msg.start();
            } else {
                logger.info("This App Service not allow Notify Alert");
            }
            // smpp SERVER
//            smppServer = new SmppSever(MyConfig.SMPP_SV_PORT, MyConfig.SMPP_SV_PROCESSOR_DEGREE);
//            smppServer.start();
            //--WebServer
            websever = new WebServer();
            websever.start();
            //--MoniterApp
            MoniterApp moa = new MoniterApp();
            moa.start();
        } catch (Exception e) {
            logger.error("Appstart.main:" + Tool.getLogMessage(e));
        }
    }

}
