/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.Tool;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author Company
 */
public class RequestMessage {

    static final Logger logger = Logger.getLogger(RequestMessage.class);
    static final ObjectMapper mapper = new ObjectMapper();
    String user;
    String pass;
    String tranId;
    String brandName;
    String phone;
    String mess;
    int dataEncode;          // 0 Ascii 1: Unicode UCS2
    String sendTime;    // Format dd/MM/yyyy hh24:mi:ss
    // Exten Info
    String ip;
    String host;

    public RequestMessage() {
        // Must have no-argument constructor
    }

    public String toJsonStr() {
        try {
            String jsonInString = mapper.writeValueAsString(this);
            return jsonInString;
        } catch (JsonProcessingException e) {
            logger.error(Tool.getLogMessage(e));
            return "Error RequestMessage Json Objec:" + e.getMessage();
        }
    }

    public static RequestMessage toObject(String jsonStr) {
        RequestMessage result = null;
        try {
            mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
            result = mapper.readValue(jsonStr, RequestMessage.class);
        } catch (IOException e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public int getDataEncode() {
        return dataEncode;
    }

    public void setDataEncode(int dataEncode) {
        this.dataEncode = dataEncode;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
