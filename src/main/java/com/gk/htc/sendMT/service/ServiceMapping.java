/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.service;

import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class ServiceMapping {

    static Logger logger = Logger.getLogger(ServiceMapping.class);
    static HashMap daHashTable = new HashMap();

    public ServiceMapping() {
    }

    public ServiceMT getInstance(String serviceCode) throws Exception {
        ServiceMT serviceIns = null;
        Class service = null;
        try {
            service = (Class) daHashTable.get(serviceCode.toUpperCase());
            if (service == null) {
                logger.error("Ma dich vu : " + serviceCode + " [chua duoc dang ky.]");
//                throw new Exception("Ma dich vu : " + serviceCode + " [chua duoc dang ky.]");
            } else {
                serviceIns = (ServiceMT) service.newInstance();
            }
        } catch (IllegalAccessException | InstantiationException e) {
            Tool.debug("ServiceMapping.getInstance:" + e.getMessage());
        }
        return serviceIns;
    }

    static void register(String serviceCode, Class daService) {
        daHashTable.put(serviceCode, daService);
    }

    static void register(String serviceCode, String daService) throws Exception {
        daHashTable.put(serviceCode, Class.forName(daService));
    }

    public static void loadService() {
        try {
            daHashTable = new HashMap();
            ArrayList<Provider> all = Provider.getALL();
            //------------------
            if (all != null && !all.isEmpty()) {
                for (Provider one : all) {
                    try {
                        register(one.getCode().toUpperCase(), one.getClassSend());
                        Tool.debug("->Ma Register:[" + one.getCode().toUpperCase() + "]: " + one.getClassSend());
                    } catch (Exception e) {
                        Tool.debug("->[" + one.getCode().toUpperCase() + "]: Not Have Class Mapping");
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(Tool.getLogMessage(ex));
        }
    }
}
