/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.service;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;

/**
 *
 * @author TUANPLA
 */
public class Send2CMC implements ServiceMT {

    @Override
    public void doSendBrand(SmsBrandQueue oneQueueBr) {
        AppStart.cmc_task.addToqueue(oneQueueBr);
    }
}
