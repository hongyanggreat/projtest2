/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.service;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.sendMT.VasVTE.ViaHTC.ClientHtcVasVte;
import com.gk.htc.sendMT.VasVTE.ViaHTC.ResultHTC;

/**
 *
 * @author TUANPLA
 */
public class Send2VasVTEViaHTC implements ServiceMT {

    @Override
    public void doSendBrand(SmsBrandQueue brQueue) {
        AppStart.vasVTE_viaHTC.addToqueue(brQueue);

    }

    public static ResultHTC doCheckBalance() {
        return ClientHtcVasVte.checkBalance();
    }

}
