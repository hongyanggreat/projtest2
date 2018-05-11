/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.Tool;
import java.io.IOException;
import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class RouteTable implements Serializable {

    private static final long serialVersionUID = 7877994872572413382L;
    static final Logger logger = Logger.getLogger(OptionTelco.class);
    static final ObjectMapper mapper = new ObjectMapper();

    public RouteTable() {
    }

    public static RouteTable json2Object(String strJson) {
        RouteTable result = new RouteTable();
        try {
            result = mapper.readValue(strJson, RouteTable.class);
        } catch (IOException e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

    public static String toStringJson(RouteTable route) {
        if (route != null) {
            try {
                String jsonInString = mapper.writeValueAsString(route);
                return jsonInString;
            } catch (JsonProcessingException e) {
                logger.error(Tool.getLogMessage(e));
                return "";
            }
        } else {
            return "";
        }
    }

    public String toStringJson() {
        try {
            String jsonInString = mapper.writeValueAsString(this);
            return jsonInString;
        } catch (JsonProcessingException e) {
            logger.error(Tool.getLogMessage(e));
            return "";
        }
    }

    public boolean checkRole(String oper, int type) {
        boolean result = false;

        if (oper.equals(SMSUtils.OPER.VIETTEL.val)) {
            if (type == BrandLabel.TYPE.CSKH.val) {
                if (!vte.getRoute_CSKH().equals("0")) {
                    result = true;
                }
            } else if (type == BrandLabel.TYPE.QC.val) {
                if (!vte.getRoute_QC().equals("0")) {
                    result = true;
                }
            } else {
                // Ke no vu khong dung kieu thi da la khong cap Phep roi
            }
        } else if (oper.equals(SMSUtils.OPER.MOBI.val)) {
            if (type == BrandLabel.TYPE.CSKH.val) {
                if (!mobi.getRoute_CSKH().equals("0")) {
                    result = true;
                }
            } else if (type == BrandLabel.TYPE.QC.val) {
                if (!mobi.getRoute_QC().equals("0")) {
                    result = true;
                }
            } else {
                // Ke no vi khong dung kieu thi da la khong cap Phep roi
            }
        } else if (oper.equals(SMSUtils.OPER.VINA.val)) {
            if (type == BrandLabel.TYPE.CSKH.val) {
                if (!vina.getRoute_CSKH().equals("0")) {
                    result = true;
                }
            } else if (type == BrandLabel.TYPE.QC.val) {
                if (!vina.getRoute_QC().equals("0")) {
                    result = true;
                }
            } else {
                // Ke no vi khong dung kieu thi da la khong cap Phep roi
            }
        } else if (oper.equals(SMSUtils.OPER.VNM.val)) {
            if (type == BrandLabel.TYPE.CSKH.val) {
                if (!vnm.getRoute_CSKH().equals("0")) {
                    result = true;
                }
            } else if (type == BrandLabel.TYPE.QC.val) {
                if (!vnm.getRoute_QC().equals("0")) {
                    result = true;
                }
            } else {
                // Ke no vi khong dung kieu thi da la khong cap Phep roi
            }
        } else if (oper.equals(SMSUtils.OPER.BEELINE.val)) {
            if (type == BrandLabel.TYPE.CSKH.val) {
                if (!bl.getRoute_CSKH().equals("0")) {
                    result = true;
                }
            } else if (type == BrandLabel.TYPE.QC.val) {
                if (!bl.getRoute_QC().equals("0")) {
                    result = true;
                }
            } else {
                // Ke no vi khong dung kieu thi da la khong cap Phep roi
            }
        } else {
            // Ke no vi no cha thuoc mang nao
        }
        return result;
    }

    public String getSendTo(String oper, int type) {
        String result = "0";

        if (oper.equals(SMSUtils.OPER.VIETTEL.val)) {
            if (type == BrandLabel.TYPE.CSKH.val) {
                if (!vte.getRoute_CSKH().equals("0")) {
                    result = vte.getRoute_CSKH();
                }
            } else if (type == BrandLabel.TYPE.QC.val) {
                if (!vte.getRoute_QC().equals("0")) {
                    result = vte.getRoute_QC();
                }
            } else {
                // Ke no vu khong dung kieu thi da la khong cap Phep roi
            }
        } else if (oper.equals(SMSUtils.OPER.MOBI.val)) {
            if (type == BrandLabel.TYPE.CSKH.val) {
                if (!mobi.getRoute_CSKH().equals("0")) {
                    result = mobi.getRoute_CSKH();
                }
            } else if (type == BrandLabel.TYPE.QC.val) {
                if (!mobi.getRoute_QC().equals("0")) {
                    result = mobi.getRoute_QC();
                }
            } else {
                // Ke no vi khong dung kieu thi da la khong cap Phep roi
            }
        } else if (oper.equals(SMSUtils.OPER.VINA.val)) {
            if (type == BrandLabel.TYPE.CSKH.val) {
                if (!vina.getRoute_CSKH().equals("0")) {
                    result = vina.getRoute_CSKH();
                }
            } else if (type == BrandLabel.TYPE.QC.val) {
                if (!vina.getRoute_QC().equals("0")) {
                    result = vina.getRoute_QC();
                }
            } else {
                // Ke no vi khong dung kieu thi da la khong cap Phep roi
            }
        } else if (oper.equals(SMSUtils.OPER.VNM.val)) {
            if (type == BrandLabel.TYPE.CSKH.val) {
                if (!vnm.getRoute_CSKH().equals("0")) {
                    result = vnm.getRoute_CSKH();
                }
            } else if (type == BrandLabel.TYPE.QC.val) {
                if (!vnm.getRoute_QC().equals("0")) {
                    result = vnm.getRoute_QC();
                }
            } else {
                // Ke no vi khong dung kieu thi da la khong cap Phep roi
            }
        } else if (oper.equals(SMSUtils.OPER.BEELINE.val)) {
            if (type == BrandLabel.TYPE.CSKH.val) {
                if (!bl.getRoute_CSKH().equals("0")) {
                    result = bl.getRoute_CSKH();
                }
            } else if (type == BrandLabel.TYPE.QC.val) {
                if (!bl.getRoute_QC().equals("0")) {
                    result = bl.getRoute_QC();
                }
            } else {
                // Ke no vi khong dung kieu thi da la khong cap Phep roi
            }
        } else {
            // Ke no vi no cha thuoc mang nao
        }
        return result;
    }

    public String getGroup(String oper) {
        String result = "0";
        if (oper.equals(SMSUtils.OPER.VIETTEL.val)) {
            if (!Tool.checkNull(vte.getGroup()) && !vte.getGroup().equals("0")) {
                result = vte.getGroup();
            }
        } else if (oper.equals(SMSUtils.OPER.MOBI.val)) {
            if (!Tool.checkNull(mobi.getGroup()) && !mobi.getGroup().equals("0")) {
                result = mobi.getGroup();
            }
        } else if (oper.equals(SMSUtils.OPER.VINA.val)) {
            if (!Tool.checkNull(vina.getGroup()) && !vina.getGroup().equals("0")) {
                result = vina.getGroup();
            }
        } else if (oper.equals(SMSUtils.OPER.VNM.val)) {
            if (!Tool.checkNull(vnm.getGroup()) && !vnm.getGroup().equals("0")) {
                result = vnm.getGroup();
            }
        } else if (oper.equals(SMSUtils.OPER.BEELINE.val)) {
            if (!Tool.checkNull(bl.getGroup()) && !bl.getGroup().equals("0")) {
                result = bl.getGroup();
            }
        }
        return result;
    }
//****
    private OperProperties vte;
    private OperProperties mobi;
    private OperProperties vina;
    private OperProperties vnm;
    private OperProperties bl;

    public OperProperties getVte() {
        if (vte == null) {
            vte = new OperProperties();
        }
        return vte;
    }

    public void setVte(OperProperties vte) {
        this.vte = vte;
    }

    public OperProperties getMobi() {
        if (mobi == null) {
            mobi = new OperProperties();
        }
        return mobi;
    }

    public void setMobi(OperProperties mobi) {
        this.mobi = mobi;
    }

    public OperProperties getVina() {
        if (vina == null) {
            vina = new OperProperties();
        }
        return vina;
    }

    public void setVina(OperProperties vina) {
        this.vina = vina;
    }

    public OperProperties getVnm() {
        if (vnm == null) {
            vnm = new OperProperties();
        }
        return vnm;
    }

    public void setVnm(OperProperties vnm) {
        this.vnm = vnm;
    }

    public OperProperties getBl() {
        if (bl == null) {
            bl = new OperProperties();
        }
        return bl;
    }

    public void setBl(OperProperties bl) {
        this.bl = bl;
    }
}
