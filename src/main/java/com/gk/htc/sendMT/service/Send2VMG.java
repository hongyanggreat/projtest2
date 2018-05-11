/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.service;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import org.apache.log4j.Logger;

/**
 *
 * @author Centurion
 */
public class Send2VMG implements ServiceMT {

    static final Logger logger = Logger.getLogger(Send2VMG.class);

    @Override
    public void doSendBrand(SmsBrandQueue brQueue) {
        AppStart.vmg_task.addToqueue(brQueue);
    }
}
