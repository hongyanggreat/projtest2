/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.common.Tool;
import java.io.Serializable;

/**
 *
 * @author TUANPLA
 */
public class OperProperties implements Serializable {

    private static final long serialVersionUID = 7887994872572413382L;
    String operCode;
    String route_QC;
    String route_CSKH;
    String group;

    public OperProperties() {
        this.operCode = "OTHER";
        this.route_QC = "0";
        this.route_CSKH = "0";
        this.group = "0";
    }

    public String getOperCode() {
        return operCode;
    }

    public void setOperCode(String operCode) {
        this.operCode = operCode;
    }

    public String getRoute_QC() {
        return route_QC;
    }

    public void setRoute_QC(String route_QC) {
        this.route_QC = route_QC;
    }

    public String getRoute_CSKH() {
        return route_CSKH;
    }

    public void setRoute_CSKH(String route_CSKH) {
        this.route_CSKH = route_CSKH;
    }

    public String getGroup() {
        if (Tool.checkNull(group)) {
            return "0";
        }
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
