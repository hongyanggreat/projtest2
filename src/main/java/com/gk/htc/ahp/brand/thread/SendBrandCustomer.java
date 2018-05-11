/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.MsgBrandCustomer;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class SendBrandCustomer extends Thread {

    final Logger logger = Logger.getLogger(SendBrandCustomer.class);
    private boolean stop = false;

    public SendBrandCustomer() {
        this.setName("SendBrandCustomer [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
    }

    @Override
    public void run() {
        try {
            Tool.debug("|===> " + this.getName() + " is Started....");
            while (AppStart.isRuning && !stop) {
                MsgBrandCustomer dao = new MsgBrandCustomer();
                ArrayList<MsgBrandCustomer> all = dao.getAll_MsgCustomer();
                if (all != null && !all.isEmpty()) {
                    logger.info("SendBrandCustomer size: " + all.size());
                    for (MsgBrandCustomer one : all) {
                        try {
                            // Chi day lai Queue gui ko log lai Income nua? Tại Sao
                            // Xoa Thanh cong thi day
                            if (dao.del(one.getId())) {
                                // TODO neu label = null --> chua den doan Account thi da bi log DB roi nen khong tim thay lich su
                                SmsBrandQueue oneQueue = one.buildQueue();
                                if (oneQueue != null) {
                                    if (oneQueue.getResult() == 1 || oneQueue.getResult() == 99) {
                                        // Add vao Queue de xu ly gui & Đã log income ngay khi buildQueue() rồi
                                        AppStart.sendPrimaryTask.addToqueue(oneQueue);
                                    } else {
                                        oneQueue.setErrorInfo("oneQueue.getResult() !=1 or 99");
                                        logger.error(oneQueue.toStringJson());
                                    }
                                } else {
                                    // Bị lỗi và bị reject đã log income trong hàm buildQueue()
                                }
                            }
                        } catch (Exception e) {
                            logger.error("SendBrandCustomer ERROR:" + one.getLabel() + ":" + one.getUserSender() + "|Exception=" + e.getMessage());
                            logger.error(Tool.getLogMessage(e));
                        }
                    }
                }
                // 30s SendBrandCustomer 1 lan neu co
                Thread.sleep(10 * 1000);
                Tool.debug("|==> SendBrandCustomer Thread is Wakeup[" + DateProc.createTimestamp() + "]....");
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        MonitorWorker.removeDemonName(this.getName());
    }

    public void shutDown() {
        stop = true;
    }
}
