/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.DoWork;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.Account;
import com.gk.htc.ahp.brand.entity.AppConfig;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.entity.CDRSubmit;
import com.gk.htc.ahp.brand.entity.Money_info;
import com.gk.htc.ahp.brand.entity.MsgBrandIncome;
import com.gk.htc.ahp.brand.entity.MsgBrandSubmit;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.sendMT.service.Send2VasVTE;
import com.gk.htc.sendMT.VasVTE.HTC.CpBalance;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class ReloadAble extends Thread {

    static Logger logger = Logger.getLogger(ReloadAble.class);

    public ReloadAble() {
        this.setName("ReloadAble [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
    }
    private static boolean REMOVE_LOG = false;
    private static boolean INSERT_KPI_REQ = false;
    private static boolean INSERT_KPI_SUBMIT = false;
    boolean stop = false;

    public void shutDown() {
        stop = true;
    }

    public static void main(String[] args) {
        System.out.println(DateProc.Timestamp2DDMMYYYY(DateProc.getNextDate(DateProc.createTimestamp(), -1)));
    }

    @Override
    public void run() {
        MsgBrandIncome incomeDao = new MsgBrandIncome();
        MsgBrandSubmit submitDao = new MsgBrandSubmit();
        Tool.debug("|===> " + this.getName() + " is started...");
        DoWork working = new DoWork();
        double hm = DateProc.getTimer();
        String date = "";
        while (AppStart.isRuning && !stop) {
            try {
                // Remove log over Week
                if (hm > 0 && hm <= 0.1) {
                    REMOVE_LOG = false;
                    INSERT_KPI_REQ = false;
                    INSERT_KPI_SUBMIT = false;
                }
                if (hm > 0.1 && hm < 0.5 && !INSERT_KPI_REQ && MyConfig.SV_ALERT) {
//                if (hm > 0.1 && hm < 10.5 && !INSERT_KPI_REQ) {
                    MyLog.debug("========>> INSERT_KPI_REQ one Node: " + MyConfig.LB_NODE);
                    date = DateProc.Timestamp2DDMMYYYY(DateProc.getNextDate(DateProc.createTimestamp(), -1));
                    incomeDao.insertKPI_Income(date);
                    INSERT_KPI_REQ = true;
                }
                if (hm > 0.1 && hm < 0.5 && !INSERT_KPI_SUBMIT && MyConfig.SV_ALERT) {
//                if (hm > 0.1 && hm < 10.5 && !INSERT_KPI_SUBMIT) {
                    MyLog.debug("========>> INSERT_KPI_SUBMIT one Node: " + MyConfig.LB_NODE);
                    date = DateProc.Timestamp2DDMMYYYY(DateProc.getNextDate(DateProc.createTimestamp(), -1));
                    submitDao.insertKPI_Submit(date);
                    INSERT_KPI_SUBMIT = true;
                }
                if (hm > 0.1 && hm < 0.5 && !REMOVE_LOG) {
                    MyLog.debug("Start-Remove-Log...");
                    incomeDao.removeInCome();
                    MsgBrandSubmit.removeSubmit();
                    REMOVE_LOG = true;
                }

                Thread.sleep(10 * 1000);
                long distance = working.done();
                if (distance / 1000 > 300) {
                    // Chay het queue nay thi reload Brand 1 lan
                    BrandLabel.reload();
                    // Reload Account
                    Account.reload();
                    // Check Amount
                    if (MyConfig.SV_ALERT) {
                        CpBalance bl = Send2VasVTE.doCheckBalance();
                        String info = "getErrCode=" + bl.getErrCode() + "|getErrCode=" + bl.getErrDesc() + "|getBalance=" + bl.getBalance();
                        Money_info.insertLog(bl.getBalance() + "", info);
                    }
                    // Reload Brand Queue Send
                    SmsQueueDao.reloadBrandLog_income();
                    //--
                    SmsQueueDao.reloadBrandLog_submit();
                    //--
                    // TODO Chỗ này để insert CDR Submit sau 5 phut
                    if (MyConfig.BUID_CDR) {
                        CDRSubmit.cdrProcess();
                    } else {
                        Tool.debug("CDRSubmit.cdrProcess() not run -> MyConfig.BUID_CDR:" + MyConfig.BUID_CDR);
                    }
                    //--
                    AppConfig.reload();
                    Tool.debug("[======> ReloadAble is live: " + DateProc.createTimestamp());
                    logger.debug("[======> ReloadAble is live: " + DateProc.createTimestamp());
                    working.doneCycle();
                }
                SmsQueueDao.reloadBrandQueueSend();
                hm = DateProc.getTimer();
            } catch (Exception e) {
                logger.error(Tool.getLogMessage(e));
            }
        }
        MonitorWorker.removeDemonName(this.getName());
    }
}
