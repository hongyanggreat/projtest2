package com.gk.htc.ahp.brand.service.primarywork;

import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.service.primarywork.PrimaryWork.TYPE_SEND;

abstract public class WorkRunnable implements Runnable {

    SmsBrandQueue brQueue;

    public void setBr(SmsBrandQueue br, TYPE_SEND type) {
        this.brQueue = br;
    }
}
