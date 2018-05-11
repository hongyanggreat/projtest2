package com.gk.htc.ahp.brand.service.primarywork;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.sendMT.service.ServiceMT;
import com.gk.htc.sendMT.service.ServiceMapping;
import org.apache.log4j.Logger;

public class PrimaryWork extends WorkRunnable {

    static final Logger logger = Logger.getLogger(PrimaryWork.class);
    //
    private static final ServiceMapping svMaping = new ServiceMapping();
    private String name;
    static final String SUCCESS_SEND = "1";
    static final String FAIL_SEND = "0";

    public PrimaryWork() {
        this.name = "PrimaryWork [" + DateProc.createTimestamp() + "]";
    }

    public static enum TYPE_SEND {

        QC, CSKH, QC_GROUP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        ServiceMT services;
        try {
            //  1.---> Lay ra Class Gui Brand
            // CSKH va QC cua Mang Khac
            services = svMaping.getInstance(brQueue.getSendTo());
            // ********* Neu lay duoc Service thi GUI TIN  *********************
            if (services == null) {
                brQueue.setResult(Tool.getInt(FAIL_SEND));
                logger.error("PrimaryWork Unknow Service send to [" + brQueue.getSendTo() + "] is null type =CSKH");
                brQueue.setErrorInfo("PrimaryWork Unknow Service send to [" + brQueue.getSendTo() + "] is null type =CSKH");
                brQueue.setCacheFrom(" PrimaryWork.class Unknow Service null");
                AppStart.log_submitTask.addToqueue(brQueue);
                MyLog.logSubmit(SmsQueueDao.toStringJson(brQueue));
                //--###--
            } else {
                // CSKH
                services.doSendBrand(brQueue);
            }
        } catch (Exception ex) {
            brQueue.setCacheFrom("PrimaryWork");
            brQueue.setErrorInfo("PrimaryWork  Service send to [" + brQueue.getSendTo() + "]:" + ex.getMessage());
            SmsQueueDao.writeBrandQueue(brQueue, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
            logger.error(brQueue.getPhone() + "=>" + Tool.getLogMessage(ex));
        }
    }

}
