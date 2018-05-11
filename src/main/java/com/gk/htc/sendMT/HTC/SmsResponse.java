/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.HTC;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.Tool;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author Private
 */
public class SmsResponse {

    static final Logger logger = Logger.getLogger(SmsResponse.class);
    static final ObjectMapper mapper = new ObjectMapper();

    public SmsResponse() {
    }

    public static SmsResponse json2Objec(String strJson) {
        SmsResponse result = null;
        try {
            result = mapper.readValue(strJson, SmsResponse.class);
        } catch (IOException e) {
            result = new SmsResponse();
            result.setStatus("Invalid String json:" + strJson);
            e.printStackTrace();
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }
    String status;
    String code;
    DataSmsResponse data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataSmsResponse getData() {
        return data;
    }

    public void setData(DataSmsResponse data) {
        this.data = data;
    }

}
