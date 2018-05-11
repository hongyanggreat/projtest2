/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Private
 */
public class ResponseMessage {

    static final ObjectMapper mapper = new ObjectMapper();
    private int code;
    private String message;
    private String transId;     // trả về TransId của hệ thống cho nhưng khách hàng không gửi TransId
    private String oper;
    private int totalSMS;
// Must have no-argument constructor

    public ResponseMessage() {
    }

    public String toJsonStr() {
        try {
            String jsonInString = mapper.writeValueAsString(this);
            return jsonInString;
        } catch (JsonProcessingException e) {
            return "Error RequestMessage Json Objec:" + e.getMessage();
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public int getTotalSMS() {
        return totalSMS;
    }

    public void setTotalSMS(int totalSMS) {
        this.totalSMS = totalSMS;
    }

}
