/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.thread;

import com.gk.htc.ahp.brand.service.primarywork.Queue;
import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.MsgBrandIncome;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class LogMsgBrandIncome extends AbstractThreadProcessQueue<SmsBrandQueue> {

    private final Logger logger = Logger.getLogger(LogMsgBrandIncome.class);
    private static final Queue<SmsBrandQueue> QUEUE_MSGBRAND_INCOME = new Queue("QUEUE_MSGBRAND_INCOME");
    MsgBrandIncome incomeDao = new MsgBrandIncome();

    public LogMsgBrandIncome() {
        super(QUEUE_MSGBRAND_INCOME);
        this.setName("LogMsgBrandIncome [" + DateProc.createTimestamp() + "]");
        MonitorWorker.addDemonName(this.getName());
    }

    @Override
    public void run() {
        Tool.debug("|===> " + this.getName() + " is started...");
        while (AppStart.isRuning && !stop) {
            try {
                SmsBrandQueue queue = queue_process.dequeue();
                if (!queue.isShutDown()) {
                    incomeDao.logIncome(queue);
                }
                Thread.sleep(1000 / AppStart.TPS_LOG);
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
        SmsBrandQueue shutDownQueue = new SmsBrandQueue();
        shutDownQueue.setShutDown(stop);
        addToqueue(shutDownQueue);
    }

    @Override
    public void StoreQueue() {
        MyLog.debug("LogMsgBrandIncome Start Store Queue " + queue_process.size());
        Tool.debug("LogMsgBrandIncome Start Store Queue " + queue_process.size());
        while (!queue_process.isEmpty()) {
            SmsBrandQueue one = queue_process.dequeue();
            one.setCacheFrom(" LogMsgBrandIncome.StoreQueue()");
            SmsQueueDao.writeBrandQueue(one, MyConfig.PATH_CACHE_LOG_MSG_BR_INCOME, ".brLog");
        }
        MyLog.debug("LogMsgBrandIncome End Store Queue " + queue_process.size());
        Tool.debug("LogMsgBrandIncome End Store Queue " + queue_process.size());
    }

    @Override
    public void addToqueue(SmsBrandQueue item) {
        this.queue_process.enqueue(item);
    }
}
