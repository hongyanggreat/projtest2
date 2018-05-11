/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.Tool;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import org.apache.log4j.Logger;

/**
 *
 * @author Centurion
 */
public class SmsBrandQueue implements Serializable, Cloneable {

    static final Logger logger = Logger.getLogger(SmsBrandQueue.class);
    private static final long serialVersionUID = 7777984872572413382L;
    static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public SmsBrandQueue clone() throws CloneNotSupportedException {
        return (SmsBrandQueue) super.clone();
    }

    public SmsBrandQueue() {
    }

    public static enum SOURCE {

        API("API"),
        SMPP("SMPP"),
        WEBSERVICE("WS"),
        CMS("CMS"),
        DONHANG("DONHANG"),;
        public String val;

        private SOURCE(String val) {
            this.val = val;
        }
    }

    public void debugValue() {
        try {
            Class objClass = this.getClass();
            // Get the public methods associated with this class.
            Method[] methods = objClass.getMethods();
            for (Method method : methods) {
                String name = method.getName();
                if (name.startsWith("get") || name.startsWith("is")) {
                    String fieldName = getFieldName(method);
                    if (!Tool.checkNull(fieldName)) {
                        if (fieldName.equals("brand") || fieldName.equals("class")) {
                            continue;
                        }
                        System.out.print(fieldName + ":" + method.invoke(this) + "\n");
                    }
                }
            }
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.error(Tool.getLogMessage(e));
        }
    }

    private static String getFieldName(Method method) {
        try {
            Class<?> clazz = method.getDeclaringClass();
            BeanInfo info = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                if (method.equals(pd.getWriteMethod()) || method.equals(pd.getReadMethod())) {
                    return pd.getName();
                }
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return null;
    }

    //------
    int id;
    String phone;
    String oper;
    String message;
    int totalSms;              // getSarTotalSegments
    String label;
    String userSender;
    Timestamp requestTime;      // TODO cho nay dang de Defaul null thi set Current Timestimep WARRNING
    Timestamp timeSend;

    int result;
    int type;
    String errorInfo;
    String tranId;          // ID cua KH gui den He Thong hoac MessageId cua he thong Tu sinh qua SMPP Server
    String messageId;       // MessageId cua SMPP gui ve khi ket noi voi nha mạng
    String sendTo;
    String brGroup;
    //-- Option for Long SMS
    int sarMsgRefNum;           // Tham so khi Ghep MT LONG
    int sarSegmentSeqnum;       // thu tu ban tin
    int sarTotalSegments;
    long processTime;
    String providerDesc;

    String node;
    String cpCode;
    //-- For Add Queue
    int retry;
    boolean shutDown;
    //    BrandLabel brand;
    // OPTIONAL FOR SMPP
    int dataEncode;
    boolean timeOut;
    long startTime;                 // For TimeOut wait Long MT
    String list_messageId;
    String reqId_vivas;
    String cacheFrom;
    String systemId;            // ID cua he thong sinh ra khi day sang các nhà cung cấp mua vào co the la Long or String
    //---
    String source;
    String optString;
    String extenInfo;       // Thong tin them

    public int getId() {
        return id;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getDataEncode() {
        return dataEncode;
    }

    public void setDataEncode(int dataEncode) {
        this.dataEncode = dataEncode;
    }

    public boolean isTimeOut() {
        return timeOut;
    }

    public void setTimeOut(boolean timeOut) {
        this.timeOut = timeOut;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getList_messageId() {
        return list_messageId;
    }

    public void setList_messageId(String list_messageId) {
        this.list_messageId = list_messageId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUserSender() {
        return userSender;
    }

    public void setUserSender(String userSender) {
        this.userSender = userSender;
    }

    public Timestamp getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(Timestamp timeSend) {
        this.timeSend = timeSend;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTotalSms() {
        return totalSms;
    }

    public void setTotalSms(int totalSms) {
        this.totalSms = totalSms;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getBrGroup() {
        return brGroup;
    }

    public void setBrGroup(String brGroup) {
        this.brGroup = brGroup;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isShutDown() {
        return shutDown;
    }

    public void setShutDown(boolean shutDown) {
        this.shutDown = shutDown;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public void setRetry() {
        this.retry = retry + 1;
    }

    public String getReqId_vivas() {
        return reqId_vivas;
    }

    public void setReqId_vivas(String reqId_vivas) {
        this.reqId_vivas = reqId_vivas;
    }

    public int getSarMsgRefNum() {
        return sarMsgRefNum;
    }

    public void setSarMsgRefNum(int sarMsgRefNum) {
        this.sarMsgRefNum = sarMsgRefNum;
    }

    public int getSarSegmentSeqnum() {
        return sarSegmentSeqnum;
    }

    public void setSarSegmentSeqnum(int sarSegmentSeqnum) {
        this.sarSegmentSeqnum = sarSegmentSeqnum;
    }

    public int getSarTotalSegments() {
        return sarTotalSegments;
    }

    public void setSarTotalSegments(int sarTotalSegments) {
        this.sarTotalSegments = sarTotalSegments;
    }

    public long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    public String getProviderDesc() {
        return providerDesc;
    }

    public void setProviderDesc(String providerDesc) {
        this.providerDesc = providerDesc;
    }

    public String getCacheFrom() {
        return cacheFrom;
    }

    public void setCacheFrom(String cacheFrom) {
        this.cacheFrom = cacheFrom;
    }

    public String getCpCode() {
        return cpCode;
    }

    public void setCpCode(String cpCode) {
        this.cpCode = cpCode;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOptString() {
        return optString;
    }

    public void setOptString(String optString) {
        this.optString = optString;
    }

    public String getExtenInfo() {
        return extenInfo;
    }

    public void setExtenInfo(String extenInfo) {
        this.extenInfo = extenInfo;
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
}
