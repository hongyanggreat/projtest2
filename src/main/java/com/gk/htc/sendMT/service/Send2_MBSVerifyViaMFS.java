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
 * @author DUONGNH
 */
public class Send2_MBSVerifyViaMFS implements ServiceMT {

    @Override
    public void doSendBrand(SmsBrandQueue brQueue) {
        AppStart.MBS_Verify_VIA_MFS_Task.addToqueue(brQueue);
    }

}
