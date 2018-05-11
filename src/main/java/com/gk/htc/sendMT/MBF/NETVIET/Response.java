/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.MBF.NETVIET;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.Tool;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author Private
 */
public class Response {

    static final Logger logger = Logger.getLogger(Response.class);
    static final ObjectMapper mapper = new ObjectMapper();
    String sid;
    String status;
    String message;

    public Response() {
        this.status = "-404";
        this.message = "Invalid Response Json";
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Response buildOBject(String str) {
        Response result;
        try {
            result = mapper.readValue(str, Response.class);
        } catch (IOException e) {
            result = new Response();
            result.setStatus("404");
            result.setMessage("Error Parser Json:" + str);
            result.setSid("-111");
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }
}
