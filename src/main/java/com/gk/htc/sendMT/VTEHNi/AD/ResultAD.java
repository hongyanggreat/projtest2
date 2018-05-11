/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.VTEHNi.AD;

/**
 *
 * @author Private
 */
public class ResultAD {

    String code;
    String message;

    public ResultAD() {
        this.code = "-1";
        this.message = "Default Result";
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
