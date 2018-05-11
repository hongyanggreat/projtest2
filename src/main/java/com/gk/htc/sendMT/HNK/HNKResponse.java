/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.HNK;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.Tool;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 *
 * @author tuanp
 */
public class HNKResponse {

    static final Logger logger = Logger.getLogger(HNKResponse.class);
    String errorid;
    String errordesc;
    String transactionId;

    public HNKResponse() {
    }

    public String getErrorid() {
        return errorid;
    }

    public void setErrorid(String errorid) {
        this.errorid = errorid;
    }

    public String getErrordesc() {
        return errordesc;
    }

    public void setErrordesc(String errordesc) {
        this.errordesc = errordesc;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    static final ObjectMapper mapper = new ObjectMapper();

    public static HNKResponse json2Object(String strJson) {
        HNKResponse result;
        try {
            result = mapper.readValue(strJson, HNKResponse.class);
        } catch (IOException e) {
            logger.error(Tool.getLogMessage(e));
            result = new HNKResponse();
            result.setErrorid("-2");
            result.setErrordesc("HNK InvalidResponse:" + strJson);
        }
        return result;
    }

}
