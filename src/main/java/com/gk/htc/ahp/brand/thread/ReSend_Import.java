/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.Account;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.entity.ResendEntity;
import com.gk.htc.ahp.brand.entity.RouteTable;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.entity.UniqueID;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.sendMT.service.ProcessSMS.CODE;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class ReSend_Import extends Thread {

    final Logger logger = Logger.getLogger(ReSend_Import.class);
    boolean stop = false;

    public ReSend_Import() {
        this.setName("ReSendError [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
    }
    private static final int MAX_TPS = 300;

    @Override
    public void run() {

        Tool.debug("|===> " + this.getName() + " is Started....");
        while (AppStart.isRuning && !stop) {
            try {
                //*** RESEND ERROR IMPORT BY ADMIN
                {
                    ResendEntity reDao = new ResendEntity();
                    ArrayList<ResendEntity> listReSend = reDao.getAllQueue(MAX_TPS);
                    if (!listReSend.isEmpty()) {
                        Tool.debug("|==> ReSendError ResendEntity: " + listReSend.size());
                        int type = BrandLabel.TYPE.CSKH.val;
                        for (ResendEntity one : listReSend) {
                            SmsBrandQueue oneCpQueue = new SmsBrandQueue();
                            oneCpQueue.setRequestTime(DateProc.createTimestamp());
                            oneCpQueue.setPhone(SMSUtils.PhoneTo84(one.getPhone()));
                            oneCpQueue.setLabel(one.getLabel());
                            oneCpQueue.setMessage(one.getMessage());
                            oneCpQueue.setType(type);                           // CSKH/QC
                            oneCpQueue.setSystemId(UniqueID.getId(oneCpQueue.getPhone()));
                            oneCpQueue.setTranId(one.getTrandId());
                            String operByPhone = SMSUtils.buildMobileOperator(one.getPhone());
                            oneCpQueue.setOper(operByPhone);
                            if (Tool.checkNull(one.getMessage())) {
                                // Log Brand Message Income
                                oneCpQueue.setResult(CODE.MSG_LENGTH_NOT_VALID.val);
                                oneCpQueue.setErrorInfo("|Message Length invalid [Length=null]| " + CODE.MESSAGE_NULL_OR_EMPTY.mess);
                                logDataInCome(oneCpQueue);
                                continue;
                            }
                            Account acc = Account.getAccount(one.getUserSender());
                            if (acc == null) {
                                oneCpQueue.setResult(CODE.LOGIN_FAIL.val);
                                oneCpQueue.setErrorInfo(CODE.LOGIN_FAIL.mess + " |userSender:" + one.getUserSender());
                                // Log Brand Message Income
                                logDataInCome(oneCpQueue);
                                continue;
                            } else if (acc.getStatus() == Account.STATUS.LOCK.val) {
                                oneCpQueue.setResult(CODE.ACC_LOCKED.val);
                                oneCpQueue.setErrorInfo(CODE.ACC_LOCKED.mess + " |userSender:" + one.getUserSender());
                                // Log Brand Message Income
                                logDataInCome(oneCpQueue);
                                continue;
                            }
                            oneCpQueue.setUserSender(acc.getUserName());
                            oneCpQueue.setCpCode(acc.getCpCode());
                            // Chu yeu chay thang nay
                            int totalMsg = SMSUtils.countSmsBrandCSKH(one.getMessage(), operByPhone);
                            if (totalMsg == SMSUtils.REJECT_MSG_LENG) {
                                oneCpQueue.setResult(CODE.MSG_LENGTH_NOT_VALID.val);
                                oneCpQueue.setErrorInfo(CODE.MSG_LENGTH_NOT_VALID.mess + " |userSender:" + one.getUserSender());
                                // Log Brand Message Income
                                logDataInCome(oneCpQueue);
                                continue;
                            }
                            oneCpQueue.setTotalSms(totalMsg);
                            BrandLabel brand = BrandLabel.findFromCache(one.getUserSender(), one.getLabel());
                            if (brand == null) {
                                oneCpQueue.setResult(CODE.BRAND_NOT_AVTIVE.val);
                                oneCpQueue.setErrorInfo(CODE.BRAND_NOT_AVTIVE.mess + " |userSender:" + acc.getUserName());
                                // Log Brand Message Income
                                logDataInCome(oneCpQueue);
                                continue;
                            }
                            RouteTable route = brand.getRoute();
                            String group = route.getGroup(operByPhone);
                            oneCpQueue.setBrGroup(group);
                            // Lay ra Huong Gui
                            String _sendTo = route.getSendTo(operByPhone, type);
                            oneCpQueue.setSendTo(_sendTo);
                            //--
                            boolean operApproved = route.checkRole(operByPhone, type);
                            if (!operApproved) {
                                oneCpQueue.setResult(CODE.SMS_TELCO_NOT_ALLOW.val);
                                oneCpQueue.setErrorInfo(CODE.SMS_TELCO_NOT_ALLOW.mess + "user:" + acc.getUserName());
                                // Log Brand Message Income
                                logDataInCome(oneCpQueue);
                                continue;
                            }
                            Tool.debug(DateProc.Timestamp2HHMMSS(1) + ": FROM [" + acc.getUserName() + "] brand[" + one.getLabel() + "] to GW=" + oneCpQueue.getSendTo());
                            //--
                            oneCpQueue.setResult(CODE.RECEIVED.val);
                            oneCpQueue.setErrorInfo(CODE.RECEIVED.mess);
                            // BO LOG DB CHUYEN NGAY VAO QUEUE GUI
                            AppStart.sendPrimaryTask.addToqueue(oneCpQueue.clone());
                            // Log Brand Message INCOME
                            logDataInCome(oneCpQueue);
                        }
                    } else {
                        Tool.debug("|===> ReSendError ResendEntity null or Empty...");
                    }
                }
                // 2 Phut Resend Error 1 lan neu co
                Thread.sleep(1 * 60 * 1000);
                Tool.debug("|==> ReSendError Thread is Wakeup[" + DateProc.createTimestamp() + "]....");
            } catch (Exception e) {
                logger.error(Tool.getLogMessage(e));
            }
        }
        MonitorWorker.removeDemonName(this.getName());
    }

    private static void logDataInCome(SmsBrandQueue oneReqQueue) {
        AppStart.log_incomeTask.addToqueue(oneReqQueue);
        MyLog.logIncome(SmsQueueDao.toStringJson(oneReqQueue));
    }

    public void shutDown() {
        stop = true;
    }
}
