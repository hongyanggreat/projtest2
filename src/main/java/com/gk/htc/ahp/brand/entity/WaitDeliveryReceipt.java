/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import java.io.Serializable;
import org.apache.log4j.Logger;
import com.gk.htc.ahp.brand.jsmpp.session.SMPPServerSession;
import com.gk.htc.ahp.brand.jsmpp.util.MessageId;

/**
 *
 * @author TUANPLA
 */
public class WaitDeliveryReceipt implements Serializable {

    static final Logger logger = Logger.getLogger(WaitDeliveryReceipt.class);
    private static final long serialVersionUID = -1334256568L;

    public long getReqTime() {
        return reqTime;
    }

    public void setReqTime(long reqTime) {
        this.reqTime = reqTime;
    }

    public Account getAcc() {
        return acc;
    }

    public void setAcc(Account acc) {
        this.acc = acc;
    }

    public SMPPServerSession getSession() {
        return session;
    }

    public void setSession(SMPPServerSession session) {
        this.session = session;
    }

    public String getKeyCacheClient() {
        return keyCacheClient;
    }

    public void setKeyCacheClient(String keyCacheClient) {
        this.keyCacheClient = keyCacheClient;
    }

    public MessageId getMessageId() {
        return messageId;
    }

    public void setMessageId(MessageId messageId) {
        this.messageId = messageId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public WaitDeliveryReceipt buildError(SMPPServerSession session) {
        WaitDeliveryReceipt result = new WaitDeliveryReceipt();
        result.setSession(session);
        return result;
    }
    private SMPPServerSession session;
    private String keyCacheClient;        // Dung de lay ra Session tra Delivery Report
    private MessageId messageId;
    private long reqTime;
    Account acc;
    private String label;
    private int type;
}
