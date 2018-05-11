/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.DoWork;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.AlertNotify;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.entity.RouteTable;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.entity.UniqueID;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.sendMT.service.ProcessSMS;
import java.util.ArrayList;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class AlertNotify_task extends Thread {

    static Logger logger = Logger.getLogger(AlertNotify_task.class);
    private int timeNotify = 0;
    private boolean stop = false;

    public AlertNotify_task() {
        this.setName("AlertNotify_task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
    }

    public void shutDown() {
        stop = true;
    }

    @Override
    public void run() {

        Tool.debug("|===> " + this.getName() + " Started....");
        DoWork working = new DoWork();
        double hm = DateProc.getTimer();
        AlertNotify alDao = new AlertNotify();
        while (AppStart.isRuning && !stop) {
            try {
                Calendar cl = Calendar.getInstance();
                int curentHour = cl.get(Calendar.HOUR_OF_DAY);
                if (hm < 1) {
                    timeNotify = 0;
                }
                if (hm >= 5) {
                    // Bat dau gui tin neu co yeu cau
                    ArrayList<AlertNotify> listMonitor = alDao.getallMonitor();
                    if (listMonitor != null && listMonitor.size() > 0) {
                        Tool.consoleOut("List notify Size:" + listMonitor.size());
                        if (curentHour == 7 || curentHour == 8 || curentHour == 9 || curentHour == 10 || curentHour == 11 || curentHour == 12
                                || curentHour == 13 || curentHour == 14 || curentHour == 15 || curentHour == 16 || curentHour == 17 || curentHour == 18
                                || curentHour == 19 || curentHour == 20 || curentHour == 21 || curentHour == 22
                                || curentHour == 23) {
                            if (curentHour > timeNotify) {
                                // Bat dau notify Monitorate
                                for (AlertNotify oneMonitor : listMonitor) {
                                    if (oneMonitor == null) {
                                        continue;
                                    }
                                    if (oneMonitor.getKind() == AlertNotify.KIND.MONITOR.val) {
                                        Tool.consoleOut("oneMonitor.getKind(): Monitor he thong");
                                        // MONITOR SPEED AN CONNECTION
                                        long currentTime = System.currentTimeMillis();
                                        if (oneMonitor.getType() == AlertNotify.TYPE.SMS.val
                                                // Thoi gian Hien tai phai lon hon thoi gian da Notify
                                                && currentTime > oneMonitor.getNextNotify()) {
                                            // SMS BRAND
                                            BrandLabel brand = BrandLabel.getFromCache(oneMonitor.getLabelId());
                                            if (brand == null) {
                                                logger.error("Get BrandLabel is null by getLabelId:" + oneMonitor.getLabelId());
                                                Tool.consoleOut("Get BrandLabel is null by getLabelId:" + oneMonitor.getLabelId());
                                                continue;
                                            }
                                            //--
                                            String _phone = SMSUtils.PhoneTo84(oneMonitor.getPhone());
                                            SmsBrandQueue oneCpQueue = new SmsBrandQueue();
                                            //--
                                            oneCpQueue.setPhone(_phone);
                                            oneCpQueue.setMessage("[" + MyConfig.LB_NODE + "]" + oneMonitor.getMessage() + "|Time Send " + DateProc.createTimestamp());
                                            oneCpQueue.setLabel(brand.getBrandLabel());
                                            oneCpQueue.setUserSender(brand.getUserOwner());
                                            oneCpQueue.setCpCode(brand.getCp_code());
                                            oneCpQueue.setType(BrandLabel.TYPE.CSKH.val);
                                            oneCpQueue.setTranId(UniqueID.getId(_phone));
                                            oneCpQueue.setSystemId(UniqueID.getId(_phone));
                                            oneCpQueue.setRequestTime(DateProc.createTimestamp());

                                            RouteTable route = brand.getRoute();

                                            oneCpQueue.setTotalSms(1);
                                            //--
                                            String operByPhone = SMSUtils.buildMobileOperator(_phone);
                                            oneCpQueue.setOper(operByPhone);
                                            String group = route.getGroup(operByPhone);
                                            oneCpQueue.setBrGroup(group);
                                            // Lay ra Huong Gui theo tin CSKH
                                            String _sendTo = route.getSendTo(operByPhone, BrandLabel.TYPE.CSKH.val);
                                            oneCpQueue.setSendTo(_sendTo);
                                            oneCpQueue.setResult(ProcessSMS.CODE.RECEIVED.val);
                                            oneCpQueue.setErrorInfo("Monitor-Alert");
                                            //--
                                            SmsBrandQueue queueSend = oneCpQueue.clone();
                                            AppStart.sendPrimaryTask.addToqueue(queueSend);
                                            //--
                                            AppStart.log_incomeTask.addToqueue(oneCpQueue);
                                            MyLog.logIncome(SmsQueueDao.toStringJson(oneCpQueue));
                                            //---UPDATE NOTIFY
                                            alDao.updateNotifyTime(oneMonitor.getNotifyId());
                                            oneMonitor.setNextNotify(currentTime + (oneMonitor.getDelay() * 60 * 1000));
                                        } else if (oneMonitor.getType() == AlertNotify.TYPE.EMAIL.val) {

                                        } else if (oneMonitor.getType() == AlertNotify.TYPE.ALL.val) {

                                        } else {
                                            logger.error("Invalid TYPE MONITOR:" + oneMonitor.getType()
                                                    + "or dif-time False| " + oneMonitor.getPhone()
                                                    + "|lbid: " + oneMonitor.getLabelId()
                                                    + "|dif-time" + (currentTime > oneMonitor.getNextNotify())
                                                    + "|ctime: " + currentTime
                                                    + "|nTime: " + oneMonitor.getNextNotify());
                                        }
                                    } else {
                                        logger.warn("Dont's check KIND:" + oneMonitor.getKind());
                                        Tool.consoleOut("Dont's check KIND:" + oneMonitor.getKind());
                                    }
                                }
                            }
                            timeNotify = curentHour;
                        }
                    } else {
                        Tool.consoleOut("List notify is null or Empty");
                    }
                }
                long distance = working.done();
                if (distance / 1000 > 180) {
                    // Chay het queue nay thi reload Notify 1 lan
                    AlertNotify.reload();
                    Tool.debug("[======> AlertNotify_task is live: " + DateProc.createTimestamp());
                    working.doneCycle();
                }
                Thread.sleep(60 * 1000);
                hm = DateProc.getTimer();
            } catch (Exception e) {
                logger.error(Tool.getLogMessage(e));
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException ex) {
                    logger.error("Sleep InterruptedException");
                }
            }
        }
        MonitorWorker.removeDemonName(this.getName());
    }

}
