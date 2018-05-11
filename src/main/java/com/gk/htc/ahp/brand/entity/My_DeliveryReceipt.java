/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.jsmpp.util.DeliveryReceiptState;
import java.io.Serializable;

/**
 *
 * @author TUANPLA
 */
public class My_DeliveryReceipt implements Serializable {

    private static final long serialVersionUID = -8138563802952633189L;
    String transId;
    String receiverPhone;
    int submitted;
    int dlvrd;
    DeliveryReceiptState status;
    String error;
    String msg;                 //  max 20 Char
    // optional
    private boolean shutDown;

    public My_DeliveryReceipt() {
    }

    public My_DeliveryReceipt(String transId,
            String receiverPhone,
            int submitted,
            int dlvrd,
            DeliveryReceiptState status,
            String error,
            String msg) {
        this.transId = transId;
        this.receiverPhone = receiverPhone;
        this.submitted = submitted;
        this.dlvrd = dlvrd;
        this.status = status;
        this.error = error;
        this.msg = msg;
    }

    public boolean isShutDown() {
        return shutDown;
    }

    public void setShutDown(boolean shutDown) {
        this.shutDown = shutDown;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public int getSubmitted() {
        return submitted;
    }

    public void setSubmitted(int submitted) {
        this.submitted = submitted;
    }

    public int getDlvrd() {
        return dlvrd;
    }

    public void setDlvrd(int dlvrd) {
        this.dlvrd = dlvrd;
    }

    public DeliveryReceiptState getStatus() {
        return status;
    }

    public void setStatus(DeliveryReceiptState status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
