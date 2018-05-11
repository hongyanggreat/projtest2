/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.resource.http;

import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.rest.RequestMessageV2;
import com.gk.htc.ahp.brand.entity.rest.ResponseMessage;
import com.gk.htc.ahp.brand.entity.UniqueID;
import com.gk.htc.sendMT.service.NewProcessV2;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.mortbay.jetty.HttpStatus;

/**
 *
 * @author Private
 */
@Path("/sms_brand_api")
public class ServiceBrandV2 {

    static final Logger logger = Logger.getLogger(ServiceBrandV2.class);
    @Context
    HttpServletRequest request;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
//    public Response doPostJson(RequestMessageV2 reqData) {
    public Response doPostJson(String data) {
        ResponseMessage resp = new ResponseMessage();
        String ip = Tool.getClientIpAddr(request);
        try {
            RequestMessageV2 reqData = RequestMessageV2.toObject(data);
            if (reqData == null) {
                resp.setCode(NewProcessV2.CODE.INVALID_JSON_DATA.val);
                resp.setMessage(NewProcessV2.CODE.INVALID_JSON_DATA.mess);
                return Response.status(HttpStatus.ORDINAL_200_OK).entity(resp.toJsonStr()).build();
            }
            String host = request.getHeader("host") + "-" + request.getRemoteHost();
            reqData.setHost(host);
            reqData.setIp(ip);
            if (Tool.checkNull(reqData.getTranId())) {
                reqData.setTranId(UniqueID.getId(reqData.getPhone()));
            }
            // Debug
            if (MyConfig.CONSOLE_OUT) {
                Tool.consoleOut("Data:" + reqData.toJsonStr());
                try {
                    Tool.consoleOut("---DO POST HEADER --->");
                    Enumeration headerParam = request.getHeaderNames();
                    while (headerParam.hasMoreElements()) {
                        String nextElement = (String) headerParam.nextElement();
                        Tool.consoleOut(nextElement + ":" + request.getHeader(nextElement));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //-- Call
            NewProcessV2 process = new NewProcessV2(reqData);
            resp = process.process();
            return Response.status(HttpStatus.ORDINAL_200_OK).entity(resp.toJsonStr()).build();
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            resp.setCode(NewProcessV2.CODE.EXCEPTION.val);
            resp.setMessage(NewProcessV2.CODE.EXCEPTION.mess);
//            resp.setTransId(UniqueID.getId(ip));     // Khong Can
            return Response.status(HttpStatus.ORDINAL_200_OK).entity(resp.toJsonStr()).build();
        }
    }

}
