/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.thread;

import com.gk.htc.ahp.brand.service.primarywork.PrimaryWork;
import com.gk.htc.ahp.brand.service.primarywork.Queue;
import com.gk.htc.ahp.brand.service.primarywork.WorkQueue;
import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class SendBrandALL extends AbstractThreadProcessQueue<SmsBrandQueue> {

    final Logger logger = Logger.getLogger(SendBrandALL.class);
    private final WorkQueue executeThreaPool;
    private static final Queue<SmsBrandQueue> QUEUE_ALL_BRAND_SEND = new Queue("QUEUE_ALL_BRAND_SEND");

    /**
     *
     * @param executeThreaPool
     * @param queue <b>Queue_Brand_Send</b>
     */
    public SendBrandALL(WorkQueue executeThreaPool) {
        super(QUEUE_ALL_BRAND_SEND);
        this.setName("SendBrandALL [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(executeThreaPool);
        this.executeThreaPool = executeThreaPool;
    }

    @Override
    public void run() {
        Tool.debug("|===> " + this.getName() + " is Started....");
        while (AppStart.isRuning && !stop) {
            try {

                SmsBrandQueue brQueue = queue_process.dequeue();
                if (!brQueue.isShutDown()) {
                    //******************Tao primaryWork ***********
                    PrimaryWork primaryWork = new PrimaryWork();
                    int type = brQueue.getType();
                    if (type == BrandLabel.TYPE.CSKH.val) {
                        primaryWork.setBr(brQueue, PrimaryWork.TYPE_SEND.CSKH);
                    }
                    if (type == BrandLabel.TYPE.QC.val) {
                        primaryWork.setBr(brQueue, PrimaryWork.TYPE_SEND.QC);
                    }
                    executeThreaPool.execute(primaryWork);
                }
            } catch (Exception e) {
                logger.error(Tool.getLogMessage(e));
            }
        }
        StoreQueue();
        MonitorWorker.removeDemonName(this.getName());
    }

    @Override
    public void shutDown() {
        stop = true;
        SmsBrandQueue oneQueue = new SmsBrandQueue();
        oneQueue.setShutDown(stop);
        addToqueue(oneQueue);
    }

    @Override
    public void StoreQueue() {
        MyLog.debug("SendBrandALL Start Store Queue " + queue_process.size());
        Tool.debug("SendBrandALL Start Store Queue " + queue_process.size());
        while (!queue_process.isEmpty()) {
            SmsBrandQueue one = queue_process.dequeue();
            one.setCacheFrom(" SendBrandALL.StoreQueue()");
            SmsQueueDao.writeBrandQueue(one, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
        }
        MyLog.debug("SendBrandALL End Store Queue " + queue_process.size());
        Tool.debug("SendBrandALL End Store Queue " + queue_process.size());
    }

    @Override
    public void addToqueue(SmsBrandQueue item) {
        if (SMSUtils.isOTP(item.getMessage())) {
            queue_process.enqueueFirst(item);
        } else {
            queue_process.enqueue(item);
        }
    }
}
