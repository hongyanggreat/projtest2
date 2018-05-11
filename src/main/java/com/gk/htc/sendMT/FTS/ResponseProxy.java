/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.FTS;

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
public class ResponseProxy {

    static final ObjectMapper mapper = new ObjectMapper();
    static final Logger logger = Logger.getLogger(ResponseProxy.class);
    String code;
    String message;
//    String transId;
//    String oper;
//    int totalSMS;

    
    public ResponseProxy() {
    }

    public ResponseProxy(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ResponseProxy toObject(String jsonStr) {
        ResponseProxy result = null;
        try {
            mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
            result = mapper.readValue(jsonStr, ResponseProxy.class);
        } catch (IOException e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

}
