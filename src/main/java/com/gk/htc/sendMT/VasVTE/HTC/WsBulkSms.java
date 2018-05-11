
package com.gk.htc.sendMT.VasVTE.HTC;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebService(name = "WsBulkSms", targetNamespace = "http://impl.bulkSms.ws/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface WsBulkSms {


    /**
     * 
     * @param cpCode
     * @param password
     * @param user
     * @return
     *     returns com.gk.htc.sendMT.VasVTE.HTC.CpBalance
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "checkBalance", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.CheckBalance")
    @ResponseWrapper(localName = "checkBalanceResponse", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.CheckBalanceResponse")
    public CpBalance checkBalance(
        @WebParam(name = "User", targetNamespace = "")
        String user,
        @WebParam(name = "Password", targetNamespace = "")
        String password,
        @WebParam(name = "CPCode", targetNamespace = "")
        String cpCode);

    /**
     * 
     * @param cpCode
     * @param password
     * @param receiverID
     * @param requestID
     * @param commandCode
     * @param serviceID
     * @param user
     * @param userID
     * @param contentType
     * @param content
     * @return
     *     returns com.gk.htc.sendMT.VasVTE.HTC.ResultBO
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "wsCpMt", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.WsCpMt")
    @ResponseWrapper(localName = "wsCpMtResponse", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.WsCpMtResponse")
    public ResultBO wsCpMt(
        @WebParam(name = "User", targetNamespace = "")
        String user,
        @WebParam(name = "Password", targetNamespace = "")
        String password,
        @WebParam(name = "CPCode", targetNamespace = "")
        String cpCode,
        @WebParam(name = "RequestID", targetNamespace = "")
        String requestID,
        @WebParam(name = "UserID", targetNamespace = "")
        String userID,
        @WebParam(name = "ReceiverID", targetNamespace = "")
        String receiverID,
        @WebParam(name = "ServiceID", targetNamespace = "")
        String serviceID,
        @WebParam(name = "CommandCode", targetNamespace = "")
        String commandCode,
        @WebParam(name = "Content", targetNamespace = "")
        String content,
        @WebParam(name = "ContentType", targetNamespace = "")
        String contentType);

    /**
     * 
     * @param cpCode
     * @param password
     * @param user
     * @param timeReport
     * @return
     *     returns java.util.List<com.gk.htc.sendMT.VasVTE.HTC.ReportHourBO>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "wsReportHour", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.WsReportHour")
    @ResponseWrapper(localName = "wsReportHourResponse", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.WsReportHourResponse")
    public List<ReportHourBO> wsReportHour(
        @WebParam(name = "User", targetNamespace = "")
        String user,
        @WebParam(name = "Password", targetNamespace = "")
        String password,
        @WebParam(name = "CPCode", targetNamespace = "")
        String cpCode,
        @WebParam(name = "timeReport", targetNamespace = "")
        String timeReport)
        throws Exception_Exception
    ;

    /**
     * 
     * @param cpCode
     * @param password
     * @param endDate
     * @param user
     * @param startDate
     * @return
     *     returns java.util.List<com.gk.htc.sendMT.VasVTE.HTC.ReportDailyBO>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "wsReportDaily", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.WsReportDaily")
    @ResponseWrapper(localName = "wsReportDailyResponse", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.WsReportDailyResponse")
    public List<ReportDailyBO> wsReportDaily(
        @WebParam(name = "User", targetNamespace = "")
        String user,
        @WebParam(name = "Password", targetNamespace = "")
        String password,
        @WebParam(name = "CPCode", targetNamespace = "")
        String cpCode,
        @WebParam(name = "startDate", targetNamespace = "")
        String startDate,
        @WebParam(name = "endDate", targetNamespace = "")
        String endDate)
        throws Exception_Exception
    ;

    /**
     * 
     * @param cpCode
     * @param password
     * @param startMonth
     * @param endMonth
     * @param user
     * @return
     *     returns java.util.List<com.gk.htc.sendMT.VasVTE.HTC.ReportMonthBO>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "wsReportMonth", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.WsReportMonth")
    @ResponseWrapper(localName = "wsReportMonthResponse", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.WsReportMonthResponse")
    public List<ReportMonthBO> wsReportMonth(
        @WebParam(name = "User", targetNamespace = "")
        String user,
        @WebParam(name = "Password", targetNamespace = "")
        String password,
        @WebParam(name = "CPCode", targetNamespace = "")
        String cpCode,
        @WebParam(name = "startMonth", targetNamespace = "")
        String startMonth,
        @WebParam(name = "endMonth", targetNamespace = "")
        String endMonth)
        throws Exception_Exception
    ;

    /**
     * 
     * @param cpCode
     * @param password
     * @param commandCode
     * @param requestMt
     * @param user
     * @return
     *     returns com.gk.htc.sendMT.VasVTE.HTC.CreateMtResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "wsCpBatchMt", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.WsCpBatchMt")
    @ResponseWrapper(localName = "wsCpBatchMtResponse", targetNamespace = "http://impl.bulkSms.ws/", className = "com.gk.htc.sendMT.VasVTE.HTC.WsCpBatchMtResponse")
    public CreateMtResult wsCpBatchMt(
        @WebParam(name = "User", targetNamespace = "")
        String user,
        @WebParam(name = "Password", targetNamespace = "")
        String password,
        @WebParam(name = "CPCode", targetNamespace = "")
        String cpCode,
        @WebParam(name = "CommandCode", targetNamespace = "")
        String commandCode,
        @WebParam(name = "requestMt", targetNamespace = "")
        List<RequestMt> requestMt);

}