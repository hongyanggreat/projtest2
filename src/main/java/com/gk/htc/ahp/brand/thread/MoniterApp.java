/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.app.SmppSever;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.service.primarywork.Queue;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.db.DBPool;
import com.gk.htc.ahp.brand.entity.Account;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.sendMT.SOUTH.Send2SouthUnicode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

public class MoniterApp extends Thread {

    final Logger logger = Logger.getLogger(MoniterApp.class);

    public MoniterApp() {
        this.setName("MoniterApp [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
    }

    @Override
    public void run() {
        Tool.debug("|===> " + this.getName() + " is started...");
        while (true) {
            try {
                InputStreamReader inR = new InputStreamReader(System.in);
                BufferedReader bR = new BufferedReader(inR);
                String input = bR.readLine();
                if (input != null) {
                    if (input.equalsIgnoreCase("racc")) {
                        Account.reload();
                        Tool.debug("Reloaded Account Completed....");
                    } else if (input.startsWith("console")) {
                        String[] arr = input.split(" ");
                        if (arr != null && arr.length == 2) {
                            MyConfig.CONSOLE_OUT = arr[1].equals("1");
                        }
                        System.out.println("Change CONSOLE_OUT: " + MyConfig.CONSOLE_OUT);
                    } else if (input.equalsIgnoreCase("show q")) {      // Show Queue
                        Queue.showQueuesSize();
                    } else if (input.equalsIgnoreCase("store")) {      // Show Queue
                        StoreQueue();
                    } else if (input.equalsIgnoreCase("show m")) {      // Show Monitor
                        MonitorWorker.ShowMonitor();
                    } else if (input.equalsIgnoreCase("show db")) {      // Show Monitor
                        DBPool.size();
                    } else if (input.equalsIgnoreCase("rsend")) {      // Show send
                        SmsQueueDao.reloadBrandQueueSend();
                    } else if (input.equalsIgnoreCase("r sm")) {
                        SmsQueueDao.reloadBrandLog_submit();
                    } else if (input.equalsIgnoreCase("r i")) {
                        SmsQueueDao.reloadBrandLog_income();
                    } else if (input.equalsIgnoreCase("check balance")) {
                        System.out.println("Tam thoi ko check");
                    } else if (input.equalsIgnoreCase("show cl")) {      // Show send
                        SmppSever.showCLient();
                    } else if (input.equalsIgnoreCase("show cache")) {
                        BrandLabel.showCache();
                    } else if (input.equalsIgnoreCase("rbrand")) {      // load cache Brand
                        BrandLabel.reload();
                        System.out.println("Reload BRand OK..");
                        // Reload Account
                        Account.reload();
                        System.out.println("Reload Account OK..");
                    } else if (input.equalsIgnoreCase("t")) {   // For Test
                        try {
                            Tool.debug("Kenh test dang tam khoa");
//                            Send2SouthUnicode.test();
                            MyLog.debug("debug loi");
//                            MyLog.logIncome("debug logIncome");
//                            MyLog.logSubmit("debug logSubmit");
                        } catch (Exception e) {
                            logger.error(Tool.getLogMessage(e));
                        }
                    } else if (input.startsWith("tms")) {
                        String[] tmp = input.split(" ");
                        if (tmp != null && tmp.length == 2) {
                            AppStart.memcache.pushMemTest(tmp[1]);
                            System.out.println("Put to Memcached: " + tmp[1]);
                        }
                    } else if (input.startsWith("tmg")) {
                        System.out.println("get Memcached test: " + AppStart.memcache.getMemTest());
                    } else if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("q")) {
                        AppStart.appStop();
                    } else if (input.equalsIgnoreCase("q m")) {
                        AppStart.isRuning = false;
                        AppStart.shutDownSmpp();
                        //--
                    } else {
                        Tool.debug("-------------\n");
                        Tool.debug("An: Q de thoat khoi chuong trinh...\n");
                        Tool.debug("An: [q m] de shutdown SMPP Client...\n");
                        Tool.debug("An: [console 1|0 ] de Bat che do console Out Put\n");
                        Tool.debug("An: [store] de Store ALL QUEUE...\n");
                        Tool.debug("An: [racc] de Reload Account...\n");

                        Tool.debug("An: [rsend] de Reload Brand Cache Send...\n");

                        Tool.debug("An: [r i] de Reload Brand imcome...\n");
                        Tool.debug("An: [r iw] de Reload Brand imcome Week...\n");

                        Tool.debug("An: [r sm] de Reload Brand Submit...\n");
                        Tool.debug("An: [r smw] de Reload Brand Submit Week...\n");

                        Tool.debug("An: [show cl] de show Client...\n");
                        Tool.debug("An: [show q] de show queue...\n");
                        Tool.debug("An: [show db] de show DB Connection Info...\n");
                        Tool.debug("An: [show m] de show MonitorWorker...\n");
                        Tool.debug("An: [show cache] de show Cache Brand Key...\n");
                        Tool.debug("An: [rbrand] de Load Cache Brand...\n");
                        Tool.debug("An: [check balance] de check balance Viettel Vas...\n");
                        Tool.debug("An: [mem lab] Check statsSlabs Memcached...\n");
                        Tool.debug("An: [mem item] Check statsItems Memcached...\n");
                    }
                }
            } catch (Exception ex) {
                logger.error(Tool.getLogMessage(ex));
            }
        }
    }

    private void StoreQueue() {
        //-- QUEUE_ALL_BRAND_SEND
        AppStart.sendPrimaryTask.StoreQueue();
        AppStart.log_incomeTask.StoreQueue();
        AppStart.log_submitTask.StoreQueue();
        //*************
        //-- QUEUE_ALL_BRAND_SEND
        AppStart.dev_task.StoreQueue();
        AppStart.antit_task.StoreQueue();
        AppStart.cmc_task.StoreQueue();
        AppStart.cmcViaMFS_task.StoreQueue();
        AppStart.hnk_task.StoreQueue();
        AppStart.mbfViaNetViet_task.StoreQueue();
        AppStart.mfs_task.StoreQueue();
        AppStart.southMFS_task.StoreQueue();
        AppStart.south_task.StoreQueue();
        AppStart.vihat_task.StoreQueue();
        AppStart.vivas_task.StoreQueue();
        AppStart.vmg_task.StoreQueue();
        AppStart.vnptMedia_task.StoreQueue();
        //        AppStart.send_vnpt.StoreQueue();
        AppStart.vnpt_nd.StoreQueue();
        AppStart.VteHni_AD.StoreQueue();
        AppStart.vasVTE_HTC.StoreQueue();
        AppStart.vasVTE_viaHTC.StoreQueue();
        AppStart.vasVTE_MFS.StoreQueue();
        AppStart.zoneSms_task.StoreQueue();

    }
}
