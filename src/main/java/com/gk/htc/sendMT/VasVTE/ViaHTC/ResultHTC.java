/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.VasVTE.ViaHTC;

/**
 *
 * @author Private
 */
public class ResultHTC {

    String code;
    String message;
    double blancer;

    public double getBlancer() {
        return blancer;
    }

    public void setBlancer(double blancer) {
        this.blancer = blancer;
    }

    public ResultHTC() {
        this.code = "-1";
        this.message = "Default Result Err";
        this.blancer=0;
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

}
