/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.HTC;

/**
 *
 * @author Private
 */
public class DataSmsResponse {

    String tranId;
    String totalSMS;
    String totalPrice;
    String invalidPhone;

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getTotalSMS() {
        return totalSMS;
    }

    public void setTotalSMS(String totalSMS) {
        this.totalSMS = totalSMS;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getInvalidPhone() {
        return invalidPhone;
    }

    public void setInvalidPhone(String invalidPhone) {
        this.invalidPhone = invalidPhone;
    }

}
