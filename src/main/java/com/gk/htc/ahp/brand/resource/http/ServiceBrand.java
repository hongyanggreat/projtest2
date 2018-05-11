/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.resource.http;

import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.sendMT.service.ProcessSMS;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

@Path("/cp/brand")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN})
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
public class ServiceBrand {

    static final Logger logger = Logger.getLogger(ServiceBrand.class);
    //Link Test http://adt.brand1.xyz:9985/service/cp/brand/CSKH?user=ahp&pass=ahpklasjdkas&phone=0982569082&brandName=AHP&mess=message&tranId=123123123

    private Response processRequest(HashMap<String, String> properties) {
        String host = Tool.getString(properties.get("host"));
        String ip = Tool.getString(properties.get("ip"));
        String user = Tool.getString(properties.get("user"));
        String pass = Tool.getString(properties.get("pass"));
        String phone = Tool.getString(properties.get("phone"));
        String brandName = Tool.getString(properties.get("brandName"));
        String function = Tool.getString(properties.get("function"));
        String mess = Tool.getString(properties.get("mess"));
        String tranId = Tool.getString(properties.get("tranId"));
        String scheduleTime = Tool.getString(properties.get("scheduleTime"));
        String rqType = Tool.getString(properties.get("rqType"));
        //===>
        ProcessSMS psms = new ProcessSMS();
        psms.setHost(host);
        psms.setIp(ip);
        psms.setUser(user);
        psms.setPass(pass);
        psms.setPhone(phone);
        psms.setBrandName(brandName);
        psms.setFunc(function);
        psms.setMess(mess);
        psms.setTranId(tranId);
        psms.setScheduleTime(scheduleTime);
        psms.setReqType(rqType);
        //--
        ProcessSMS.CODE code = psms.process();
        return Response.status(Response.Status.OK.getStatusCode())
                .entity(psms.buiildRsp(code))
                .header("Content-Type", rqType)
                .build();
    }

    //------GET FORM---------
    @GET
    @Path("/{param}")
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN})
    public Response doGet(@PathParam("param") String path, @Context UriInfo uriInfo, @Context HttpServletRequest request) {
        try {
            String ip = Tool.getClientIpAddr(request);
            String host = request.getHeader("host");
            MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
            HashMap<String, String> pop = new HashMap<>();
            pop.put("function", path);
            pop.put("host", host);
            pop.put("ip", ip);
            pop.put("rqType", MediaType.APPLICATION_FORM_URLENCODED);
            Set<String> key = queryParams.keySet();
            for (String name : key) {
                String value = queryParams.getFirst(name);
                if (MyConfig.CONSOLE_OUT) {
                    Tool.consoleOut(name + ":" + value);
                }
                pop.put(name, value);
            }
            if (MyConfig.CONSOLE_OUT) {
                try {
                    Tool.consoleOut("---DO GET --->");
                    Enumeration headerParam = request.getHeaderNames();
                    while (headerParam.hasMoreElements()) {
                        String nextElement = (String) headerParam.nextElement();
                        Tool.consoleOut(nextElement + ":" + request.getHeader(nextElement));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return processRequest(pop);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            return Response.status(Response.Status.OK.getStatusCode()).entity(ProcessSMS.CODE.EXCEPTION.getResult()).build();
        }
    }

    //------POST FORM---------
    @POST
    @Path("/{param}")
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN})
    public Response doPost(@PathParam("param") String path,
            MultivaluedMap<String, String> data, @Context HttpServletRequest request) {
        try {
            String ip = Tool.getClientIpAddr(request);
            String host = request.getHeader("host");
            HashMap<String, String> properties = new HashMap<>();
            properties.put("function", path);
            properties.put("host", host);
            properties.put("ip", ip);
            properties.put("rqType", MediaType.APPLICATION_FORM_URLENCODED);
            Set<String> datakey = data.keySet();
            for (String name : datakey) {
                String value = data.getFirst(name);
                if (MyConfig.CONSOLE_OUT) {
                    Tool.consoleOut(name + ":" + value);
                }
                properties.put(name, value);
            }
            if (MyConfig.CONSOLE_OUT) {
                try {
                    Tool.consoleOut("---DO POST --->");
                    Enumeration headerParam = request.getHeaderNames();
                    while (headerParam.hasMoreElements()) {
                        String nextElement = (String) headerParam.nextElement();
                        Tool.consoleOut(nextElement + ":" + request.getHeader(nextElement));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return processRequest(properties);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            return Response.status(200).entity(ProcessSMS.CODE.EXCEPTION.getResult()).build();
        }
    }
}
