/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.SOUTH;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.Tool;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author tuanp
 */
public class SouthResponse {

    static final Logger logger = Logger.getLogger(SouthResponse.class);
    static final ObjectMapper mapper = new ObjectMapper();
    private String status;
    private String errorcode;
    private String description;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public static SouthResponse toObject(String intput) {
        SouthResponse result;
        try {
            result = mapper.readValue(intput, SouthResponse.class);
        } catch (IOException e) {
            logger.error(Tool.getLogMessage(e));
            result = new SouthResponse();
            result.setStatus("-1");
            result.setDescription("Invalid Json Response:" + intput);

        }
        return result;
    }
}
