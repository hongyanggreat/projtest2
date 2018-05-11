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
public class HuongDEV_Taks extends _AbstractThreadSend2Provider {

    private static final Logger logger = Logger.getLogger(HuongDEV_Taks.class);

    private static final WorkQueue WORKQUEUE_DEV = new WorkQueue("WORKQUEUE_DEV", 5);
    private static final String QUEUE_DEV_NO_SEND = "Q-->QUEUE_DEV_NO_SEND";

    public HuongDEV_Taks() {
        super(QUEUE_DEV_NO_SEND);
        TPS = WORKQUEUE_DEV.getMaxPoolSize();
        this.setName("HuongDEV_Taks [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
        MonitorWorker.addWorkQueue(WORKQUEUE_DEV);
    }

    @Override
    public void run() {
        try {
            Tool.debug(this.getName() + " is started with tps = [" + TPS + "]...");
            while (AppStart.isRuning & !stop) {
                SmsBrandQueue oneQueueBr = queue.dequeue();
                if (!oneQueueBr.isShutDown()) {
                    WORKQUEUE_DEV.execute(doSendBrand_runnable(oneQueueBr));
                }
                Thread.sleep(1000 / TPS);
            }
            MonitorWorker.removeDemonName(this.getName());
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
    }

    @Override
    protected Runnable doSendBrand_runnable(final SmsBrandQueue oneQueue) {
        return new Runnable() {

            @Override
            public void run() {
                oneQueue.debugValue();
            }
        };
    }

}
