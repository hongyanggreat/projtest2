/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.VMG;

import com.gk.htc.sendMT.service.thread.VMG_Task;

/**
 *
 * @author Private
 */
public class ResultVMG {

    int error_code;
    String error_detail;
    String messageId;

    public ResultVMG() {
        this.error_code = VMG_Task.CODE.ERR_PASER.val;
        this.error_detail = VMG_Task.CODE.ERR_PASER.mess;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_detail() {
        return error_detail;
    }

    public void setError_detail(String error_detail) {
        this.error_detail = error_detail;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

}
