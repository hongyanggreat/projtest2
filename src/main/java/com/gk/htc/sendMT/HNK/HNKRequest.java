/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.HNK;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.Tool;
import org.apache.log4j.Logger;

/**
 *
 * @author tuanp
 */
public class HNKRequest {

    static final Logger logger = Logger.getLogger(HNKRequest.class);
    static final ObjectMapper mapper = new ObjectMapper();
    String msisdn;
    String brandname;
    String message;
    String typemsg;
    String username;
    String password;
    String signature;

    public HNKRequest() {
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTypemsg() {
        return typemsg;
    }

    public void setTypemsg(String typemsg) {
        this.typemsg = typemsg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String toJson() {
        try {
            String jsonInString = mapper.writeValueAsString(this);
            return jsonInString;
        } catch (JsonProcessingException e) {
            logger.error(Tool.getLogMessage(e));
            return "";
        }
    }
}
