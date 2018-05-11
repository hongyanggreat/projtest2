/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.SOUTH;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.Tool;
import org.apache.log4j.Logger;

/**
 *
 * @author tuanp
 */
public class SouthRequest {

    static final Logger logger = Logger.getLogger(SouthRequest.class);
    static final ObjectMapper mapper = new ObjectMapper();
    String from;
    String to;
    String text;
    int unicode;
    String dlr;
    String smsid;
    String campaignid;

    public SouthRequest() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUnicode() {
        return unicode;
    }

    public void setUnicode(int unicode) {
        this.unicode = unicode;
    }

    public String getDlr() {
        return dlr;
    }

    public void setDlr(String dlr) {
        this.dlr = dlr;
    }

    public String getSmsid() {
        return smsid;
    }

    public void setSmsid(String smsid) {
        this.smsid = smsid;
    }

    public String getCampaignid() {
        return campaignid;
    }

    public void setCampaignid(String campaignid) {
        this.campaignid = campaignid;
    }

    public String toJson() {
        String result = "";
        try {
            mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
            String jsonInString = mapper.writeValueAsString(this);
            result = jsonInString;
        } catch (JsonProcessingException e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }
}
