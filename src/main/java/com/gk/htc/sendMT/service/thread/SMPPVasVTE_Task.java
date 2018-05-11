/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.service.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.ahp.brand.service.primarywork.WorkQueue;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class SMPPVasVTE_Task extends _AbstractThreadSend2Provider {

    private static final Logger logger = Logger.getLogger(SMPPVasVTE_Task.class);
    private static final WorkQueue WORKQUEUE_SMPP_VAS_BULK = new WorkQueue("WORKQUEUE_SMPP_VAS_BULK", 20);
    private static final String QUEUE_NAME = "Q-->SEND_TO_SMPP_VAS_BULK";

    public SMPPVasVTE_Task() {
        super(QUEUE_NAME);
        TPS = WORKQUEUE_SMPP_VAS_BULK.getMaxPoolSize();
        this.setName("SMPPVasVTE_Task [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_SMPP_VAS_BULK);
    }

    @Override
    public void run() {
        Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
        while (AppStart.isRuning && !stop) {
            try {
//                if (AppStart.smppcl_Bulk.isConnect()) {
//                    SmsBrandQueue oneQueueBr = SEND_TO_SMPP_VAS_BULK.dequeue();
//                    try {
//
//                        if (!oneQueueBr.isShutDown()) {
//                            String message = oneQueueBr.getMessage();
//                            if (message.length() > 160) {
//                                // Long SMS
//                                // Build lai Vi quy chuan SMPP khac quy chuan Cua Telco Theo Hop Dong
//                                ArrayList<SmsBrandQueue> arrLong = SmsQueueDao.buildLongList(oneQueueBr);
//                                AppStart.smppcl_Bulk.doSendLongMT(WORKQUEUE_SMPP_VAS_BULK, arrLong);
//                            } else {
//                                // Nomal SMS
//                                AppStart.smppcl_Bulk.doSendMT(WORKQUEUE_SMPP_VAS_BULK, oneQueueBr);
//                            }
//                        }
//                        Thread.sleep(1000 / TPS);
//                    } catch (CloneNotSupportedException | InterruptedException e) {
//                        logger.error(Tool.getLogMessage(e));
//                        // TODO ERROR
//                        oneQueueBr.setCacheFrom("[-->Send2SMPP_VasVTE_Task: " + e.getMessage());
//                        SmsQueueDao.writeBrandQueue(oneQueueBr, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
//                    }
//                } else {
                Thread.sleep(2 * 1000);
//                }
            } catch (Exception e) {
                logger.error(Tool.getLogMessage(e));
            }
        }
        StoreQueue();
        MonitorWorker.removeDemonName(this.getName());
    }

    @Override
    protected Runnable doSendBrand_runnable(SmsBrandQueue oneQueue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
