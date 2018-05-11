/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.Tool;
import java.io.IOException;
import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 *
 * @author tuanp
 */
public class OptionTelco implements Serializable {

    static final Logger logger = Logger.getLogger(OptionTelco.class);
    static final ObjectMapper mapper = new ObjectMapper();
    private OptionVina vinaphone;
    private OptionCheckDuplicate checkDuplicate;

    public OptionTelco() {
        vinaphone = new OptionVina();
        checkDuplicate = new OptionCheckDuplicate();
    }

    public OptionVina getVinaphone() {
        return vinaphone;
    }

    public void setVinaphone(OptionVina vinaphone) {
        this.vinaphone = vinaphone;
    }

    public OptionCheckDuplicate getCheckDuplicate() {
        return checkDuplicate;
    }

    public void setCheckDuplicate(OptionCheckDuplicate checkDuplicate) {
        this.checkDuplicate = checkDuplicate;
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

    public static OptionTelco json2Objec(String strJson) {
        OptionTelco result = null;
        try {
            result = mapper.readValue(strJson, OptionTelco.class);
        } catch (IOException e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }
}
