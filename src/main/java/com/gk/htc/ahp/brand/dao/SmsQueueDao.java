/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.entity.RouteTable;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Centurion
 */
public class SmsQueueDao {

    static final Logger logger = Logger.getLogger(SmsBrandQueue.class);
    static final ObjectMapper mapper = new ObjectMapper();

    public SmsQueueDao() {
    }

    public static ArrayList<SmsBrandQueue> buildLongList(SmsBrandQueue brQueue) throws CloneNotSupportedException {
        ArrayList<SmsBrandQueue> list = new ArrayList<>();
        String[] arrInfo = SMSUtils.splitString(brQueue.getMessage(), 153);
        int i = 1;
        for (String oneinfo : arrInfo) {
            SmsBrandQueue one = brQueue.clone();
            one.setMessage(oneinfo);
            // TODO Phai gan lai = 1 vi qua SMPP bi chat tin thanh tung Tin nho
            one.setTotalSms(1);
            one.setSarTotalSegments(arrInfo.length);    // cai nay de gui sang viettel
            one.setSarSegmentSeqnum(i);
            list.add(one);
            i++;
        }
        return list;
    }

    public static String toStringJson(SmsBrandQueue queue) {
        if (queue != null) {
            try {
                String jsonInString = mapper.writeValueAsString(queue);
//                System.out.println("jsonInString" + jsonInString);
                return jsonInString;
            } catch (Exception e) {
//                System.out.println("e json:" + e);
                logger.error(Tool.getLogMessage(e));
                return "";
            }
        } else {
            return "";
        }
    }

    public static SmsBrandQueue json2Object(String json) {
        SmsBrandQueue result = null;
        try {
            return mapper.readValue(json, SmsBrandQueue.class);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

//    public static SmsBrandQueue json2Object(String str) {
//        SmsBrandQueue result = null;
//        if (!Tool.checkNull(str)) {
//            result = new SmsBrandQueue();
//            JSONObject tmp = JSONObject.fromObject(str);
//            if (tmp.getString("id") != null) {
//                result.setId(tmp.getInt("id"));
//            }
//            if (tmp.getString("phone") != null) {
//                result.setPhone(tmp.getString("phone"));
//            }
//            if (tmp.getString("oper") != null) {
//                result.setOper(tmp.getString("oper"));
//            }
//            if (tmp.getString("message") != null) {
//                result.setMessage(tmp.getString("message"));
//            }
//            if (tmp.getString("totalSms") != null) {
//                result.setTotalSms(tmp.getInt("totalSms"));
//            }
//            if (tmp.getString("label") != null) {
//                result.setLabel(tmp.getString("label"));
//            }
//            if (tmp.getString("userSender") != null) {
//                result.setUserSender(tmp.getString("userSender"));
//            }
//            if (tmp.getString("requestTime") != null) {
//                String requestTime = tmp.getString("requestTime");
//                Timestamp rqTime = json2Timestamp(requestTime);
//                result.setRequestTime(rqTime);
//            }
//            if (tmp.getString("timeSend") != null) {
//                String requestTime = tmp.getString("timeSend");
//                Timestamp sendTime = json2Timestamp(requestTime);
//                result.setTimeSend(sendTime);
//            }
//            if (tmp.getString("result") != null) {
//                result.setResult(tmp.getInt("result"));
//            }
//            if (tmp.getString("type") != null) {
//                result.setType(tmp.getInt("type"));
//            }
//            if (tmp.getString("errorInfo") != null) {
//                result.setErrorInfo(tmp.getString("errorInfo"));
//            }
//            if (tmp.getString("tranId") != null) {
//                result.setTranId(tmp.getString("tranId"));
//            }
//            if (tmp.getString("messageId") != null) {
//                result.setMessageId(tmp.getString("messageId"));
//            }
//            if (tmp.getString("sendTo") != null) {
//                result.setSendTo(tmp.getString("sendTo"));
//            }
//            if (tmp.getString("brGroup") != null) {
//                result.setBrGroup(tmp.getString("brGroup"));
//            }
//            if (tmp.getString("sarMsgRefNum") != null) {
//                result.setSarMsgRefNum(tmp.getInt("sarMsgRefNum"));
//            }
//            if (tmp.getString("sarSegmentSeqnum") != null) {
//                result.setSarSegmentSeqnum(tmp.getInt("sarSegmentSeqnum"));
//            }
//            if (tmp.getString("sarTotalSegments") != null) {
//                result.setSarTotalSegments(tmp.getInt("sarTotalSegments"));
//            }
//            if (tmp.getString("processTime") != null) {
//                result.setProcessTime(tmp.getInt("processTime"));
//            }
//            if (tmp.getString("providerDesc") != null) {
//                result.setProviderDesc(tmp.getString("providerDesc"));
//            }
//            if (tmp.getString("node") != null) {
//                result.setNode(tmp.getString("node"));
//            }
//            if (tmp.getString("cpCode") != null) {
//                result.setCpCode(tmp.getString("cpCode"));
//            }
//
//            if (tmp.getString("retry") != null) {
//                result.setRetry(tmp.getInt("retry"));
//            }
//            if (tmp.getString("shutDown") != null) {
//                result.setShutDown(tmp.getBoolean("shutDown"));
//            }
//            if (tmp.getString("dataEncode") != null) {
//                try {
//                    result.setDataEndCode((byte) tmp.getInt("dataEncode"));
//                } catch (Exception e) {
//                    logger.error(Tool.getLogMessage(e));
//                }
//            }
//            if (tmp.getString("timeOut") != null) {
//                result.setTimeOut(tmp.getBoolean("timeOut"));
//            }
//            if (tmp.getString("startTime") != null) {
//                result.setStartTime(tmp.getLong("startTime"));
//            }
//            if (tmp.getString("list_messageId") != null) {
//                result.setList_messageId(tmp.getString("list_messageId"));
//            }
//            if (tmp.getString("reqId_vivas") != null) {
//                result.setReqId_vivas(tmp.getString("reqId_vivas"));
//            }
//            if (tmp.getString("cacheFrom") != null) {
//                result.setCacheFrom(tmp.getString("cacheFrom"));
//            }
//            if (tmp.getString("systemId") != null) {
//                result.setSystemId(tmp.getString("systemId"));
//            }
//            if (tmp.getString("source") != null) {
//                result.setSource(tmp.getString("source"));
//            }
//            if (tmp.getString("optString") != null) {
//                result.setOptString(tmp.getString("optString"));
//            }
//            if (tmp.getString("extenInfo") != null) {
//                result.setExtenInfo(tmp.getString("extenInfo"));
//            }
//        }
//        return result;
//    }
//
//    public static Timestamp json2Timestamp(String json) {
//        Timestamp result = null;
//        try {
//            if (!Tool.checkNull(json)) {
//                JSONObject jobj = JSONObject.fromObject(json);
//                Date d = (Date) JSONObject.toBean(jobj, Date.class);
//                if (d != null) {
//                    result = new Timestamp(d.getTime());
//                } else {
//                    if (jobj.getString("time") != null) {
//                        long longTime = jobj.getLong("time");
//                        result = new Timestamp(longTime);
//                    } else {
//                        result = new Timestamp(jobj.getInt("year"), jobj.getInt("month"), jobj.getInt("date"), jobj.getInt("hours"), jobj.getInt("minutes"), jobj.getInt("seconds"), jobj.getInt("nanos"));
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error(Tool.getLogMessage(e));
//        }
//        return result;
//    }
    public static void writeBrandQueue(SmsBrandQueue data, String pathCahe, String extention) {
        FileOutputStream oneFile = null;
        try {
            oneFile = new FileOutputStream(pathCahe + "/" + data.getTranId() + "_" + System.nanoTime() + extention);
            try (Writer outw2 = new BufferedWriter(new OutputStreamWriter(oneFile, "UTF-8"))) {
                outw2.write(toStringJson(data));
            }
        } catch (IOException ex) {
            logger.error(Tool.getLogMessage(ex));
            Tool.debug("Write writeBrandQueueSend Error: " + ex.getMessage());
        } finally {
            try {
                if (oneFile != null) {
                    oneFile.close();
                }
            } catch (IOException e) {
                logger.error(Tool.getLogMessage(e));
            }
        }
    }

    public static void reloadBrandQueueSend() {
        SmsBrandQueue data;
        try {
            File direc = new File(MyConfig.PATH_CACHE_BRAND_SEND);
            String[] arrfile = direc.list();
            File filedel;
            if (arrfile != null && arrfile.length > 0) {
                int i = 1;
                for (String one : arrfile) {
                    try {
                        String dataStr = Tool.readFileText(MyConfig.PATH_CACHE_BRAND_SEND + "/" + one);
                        // Load MO
                        data = json2Object(dataStr);
                        if (data != null && !data.isShutDown()) {
                            String phone = SMSUtils.PhoneTo84(data.getPhone());
                            String oper = SMSUtils.buildMobileOperator(phone);
                            BrandLabel brand = BrandLabel.findFromCache(data.getUserSender(), data.getLabel());
                            String _sendTo = "";    // Mac dinh _sendto= ""
                            if (brand != null) {
                                // Lay ra Huong Gui moi neu co
                                RouteTable route = brand.getRoute();
                                _sendTo = route.getSendTo(data.getOper(), data.getType());
                            }
                            // Dat lai Huong Gui Moi
                            data.setSendTo(_sendTo);
                            // Dat lai nha mang
                            data.setOper(oper);
                            // Valid lai So dien thoai
                            data.setPhone(phone);
                            // Dai lai CP Code ???
                            data.setCpCode(data.getCpCode()); // SET CP CODE
                            AppStart.sendPrimaryTask.addToqueue(data);
                            if (i == 1) {
                                data.debugValue();
                            }
                            i++;
                        }
                        filedel = new File(MyConfig.PATH_CACHE_BRAND_SEND + "/" + one);
                        filedel.delete();
                    } catch (Exception ex) {
                        logger.error(Tool.getLogMessage(ex));
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(Tool.getLogMessage(ex));
            Tool.debug("[=====>>reloadBrandQueueSend Cache Loi!");
        }
    }

    public static void reloadBrandLog_income() {
        SmsBrandQueue data;
        try {
            File direc = new File(MyConfig.PATH_CACHE_LOG_MSG_BR_INCOME);
            String[] arrfile = direc.list();
            File filedel;
            if (arrfile != null && arrfile.length > 0) {
                for (String one : arrfile) {
                    try {
                        String dataStr = Tool.readFileText(MyConfig.PATH_CACHE_LOG_MSG_BR_INCOME + "/" + one);
                        // Load MO
                        data = json2Object(dataStr);
                        if (data != null) {
                            AppStart.log_incomeTask.addToqueue(data);
                        }
                        filedel = new File(MyConfig.PATH_CACHE_LOG_MSG_BR_INCOME + "/" + one);
                        filedel.delete();
                    } catch (Exception ex) {
                        logger.error(Tool.getLogMessage(ex));
                    }
                }
                Tool.debug("[=====>>reloadBrandLong_income --> [OK]");
            }
        } catch (Exception ex) {
            logger.error(Tool.getLogMessage(ex));
            Tool.debug("[=====>>reloadBrandLong_income Cache Loi!");
        }
    }

    public static void reloadBrandLog_submit() {
        SmsBrandQueue data;
        try {
            File direc = new File(MyConfig.PATH_CACHE_LOG_MSG_BR_SUBMIT);
            String[] arrfile = direc.list();
            File filedel;
            if (arrfile != null && arrfile.length > 0) {
                for (String one : arrfile) {
                    try {
                        String dataStr = Tool.readFileText(MyConfig.PATH_CACHE_LOG_MSG_BR_SUBMIT + "/" + one);
                        // Load MO
                        data = json2Object(dataStr);
                        if (data != null) {
                            if (!data.isShutDown()) {
                                AppStart.log_submitTask.addToqueue(data);
                            }
                        }
                        filedel = new File(MyConfig.PATH_CACHE_LOG_MSG_BR_SUBMIT + "/" + one);
                        filedel.delete();
                    } catch (Exception ex) {
                        logger.error(Tool.getLogMessage(ex));
                    }
                }
                Tool.debug("[=====>>reloadBrandLong_submit --> [OK]");
            }
        } catch (Exception ex) {
            logger.error(Tool.getLogMessage(ex));
            Tool.debug("[=====>>reloadBrandLong_submit Cache Loi!");
        }
    }

    public static void readQueueDebug() {
        SmsBrandQueue data;
        try {
            try {
                String dataStr = Tool.readFileText("D:\\Error\\null_469429628274558.brSend");
                // Load MO
                data = json2Object(dataStr);
                if (data != null) {
                    data.debugValue();
                }

            } catch (Exception ex) {
                logger.error(Tool.getLogMessage(ex));
            }
        } catch (Exception ex) {
            logger.error(Tool.getLogMessage(ex));
            Tool.debug("[=====>>reloadBrandQueueSend Cache Loi!");
        }
    }

}
