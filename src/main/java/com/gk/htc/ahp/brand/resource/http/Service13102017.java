/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.resource.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.rest.RequestMessage;
import com.gk.htc.sendMT.service.ProcessSMS;
import java.io.IOException;
import java.io.StringReader;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Path("/send_sms")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN}) // Response Mime Type
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})  // Accept Mime Type
public class Service13102017 {

    static final Logger logger = Logger.getLogger(Service13102017.class);
    static final ObjectMapper mapper = new ObjectMapper();
    //Link Test http://api.brand1.xyz:9983/service/cp/test/CSKH?user=ahp&pass=ahpklasjdkas&phone=0982569082&brandName=AHP&mess=message&tranId=123123123

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
    @Consumes({MediaType.TEXT_HTML, MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED})
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
            Set<String> key = queryParams.keySet();
            for (String name : key) {
                String value = queryParams.getFirst(name);
                if (MyConfig.CONSOLE_OUT) {
                    Tool.consoleOut(name + ":" + value);
                }
                pop.put(name, value);
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
    @Consumes({MediaType.TEXT_HTML, MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED})
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
            Set<String> datakey = data.keySet();
            for (String name : datakey) {
                String value = data.getFirst(name);
                if (MyConfig.CONSOLE_OUT) {
                    Tool.consoleOut(name + ":" + value);
                }
                properties.put(name, value);
            }

            return processRequest(properties);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            return Response.status(200).entity(ProcessSMS.CODE.EXCEPTION.getResult()).build();
        }
    }

    //------GET XML---------
    @GET
    @Produces({MediaType.TEXT_XML + ";charset=utf-8", MediaType.APPLICATION_XML + ";charset=utf-8"})
    @Consumes({MediaType.APPLICATION_XML})
    @Path("/{param}")       // is function  send_sms
    public Response doGetXML(@PathParam("param") String func, @QueryParam("data") String data, @Context HttpServletRequest request) {
        try {
            String ip = Tool.getClientIpAddr(request);
            RequestMessage dataObj = readDataXML(data);
            if (dataObj == null) {
                ProcessSMS psms = new ProcessSMS(func, MediaType.APPLICATION_XML);
                return Response.status(200).entity(psms.buiildRsp(ProcessSMS.CODE.INVALID_XML_DATA)).build();
            }
            String host = request.getHeader("host") + "-" + request.getRemoteHost();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("function", func);
            properties.put("host", host);
            properties.put("ip", ip);
            properties.put("user", dataObj.getUser());
            properties.put("pass", dataObj.getPass());
            properties.put("phone", dataObj.getPhone());
            properties.put("brandName", dataObj.getBrandName());
            properties.put("mess", dataObj.getMess());
            properties.put("tranId", dataObj.getTranId());
            properties.put("rqType", MediaType.APPLICATION_XML);
            // Debug
            if (MyConfig.CONSOLE_OUT) {
                Tool.consoleOut("-------------doGetXML Param Value--------------------");
                Set<String> datakey = properties.keySet();
                for (String paramName : datakey) {
                    String value = properties.get(paramName);
                    properties.put(paramName, value);
                    Tool.consoleOut(paramName + ":" + value);
                }
            }
            //-- Call
            return processRequest(properties);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            ProcessSMS psms = new ProcessSMS(func, MediaType.APPLICATION_XML);
            return Response.status(200).entity(psms.buiildRsp(ProcessSMS.CODE.EXCEPTION)).build();
        }
    }

    //------POST XML---------
    @POST
    @Produces({MediaType.TEXT_XML + ";charset=utf-8", MediaType.APPLICATION_XML + ";charset=utf-8"})
    @Consumes({MediaType.APPLICATION_XML})
    @Path("/{param}")       // is function  send_sms
    public Response doPostXML(@PathParam("param") String func, String data, @Context HttpServletRequest request) {
        try {
            String ip = Tool.getClientIpAddr(request);
//            System.out.println("Post Data: " + data);
            RequestMessage dataObj = readDataXML(data);
            if (dataObj == null) {
                ProcessSMS psms = new ProcessSMS(func, MediaType.APPLICATION_XML);
                return Response.status(200).entity(psms.buiildRsp(ProcessSMS.CODE.INVALID_XML_DATA)).build();
            }
            String host = request.getHeader("host") + "-" + request.getRemoteHost();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("function", func);
            properties.put("host", host);
            properties.put("ip", ip);
            properties.put("user", dataObj.getUser());
            properties.put("pass", dataObj.getPass());
            properties.put("phone", dataObj.getPhone());
            properties.put("brandName", dataObj.getBrandName());
            properties.put("mess", dataObj.getMess());
            properties.put("tranId", dataObj.getTranId());
            properties.put("rqType", MediaType.APPLICATION_XML);
            // Debug
            if (MyConfig.CONSOLE_OUT) {
                Tool.consoleOut("-------------doPostXML Param Value--------------------");
                Set<String> datakey = properties.keySet();
                for (String paramName : datakey) {
                    String value = properties.get(paramName);
                    properties.put(paramName, value);
                    Tool.consoleOut(paramName + ":" + value);
                }
            }
            //-- Call
            return processRequest(properties);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            ProcessSMS psms = new ProcessSMS(func, MediaType.APPLICATION_XML);
            return Response.status(200).entity(psms.buiildRsp(ProcessSMS.CODE.EXCEPTION)).build();
        }
    }

    //------GET JSON---------
    @GET
    @Path("/{param}")       // is function send_sms
    @Produces({MediaType.APPLICATION_JSON + ";charset=utf-8", MediaType.TEXT_HTML})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doGetJson(@PathParam("param") String func, @QueryParam("data") String data, @Context HttpServletRequest request) {
        try {
//            System.out.println("doGetJson data: " + data);
            String ip = Tool.getClientIpAddr(request);
            RequestMessage dataObj = readDataJson(data);
            if (dataObj == null) {
                ProcessSMS psms = new ProcessSMS(func, MediaType.APPLICATION_JSON);
                return Response.status(200).entity(psms.buiildRsp(ProcessSMS.CODE.INVALID_JSON_DATA)).build();
            }
            String host = request.getHeader("host") + "-" + request.getRemoteHost();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("function", func);
            properties.put("host", host);
            properties.put("ip", ip);
            properties.put("user", dataObj.getUser());
            properties.put("pass", dataObj.getPass());
            properties.put("phone", dataObj.getPhone());
            properties.put("brandName", dataObj.getBrandName());
            properties.put("mess", dataObj.getMess());
            properties.put("tranId", dataObj.getTranId());
            properties.put("rqType", MediaType.APPLICATION_JSON);
            // Debug
            if (MyConfig.CONSOLE_OUT) {
                Tool.consoleOut("-------------doGetJson Param Value--------------------");
                Set<String> datakey = properties.keySet();
                for (String paramName : datakey) {
                    String value = properties.get(paramName);
                    properties.put(paramName, value);
                    Tool.consoleOut(paramName + ":" + value);
                }
            }
            //-- Call
            return processRequest(properties);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            ProcessSMS psms = new ProcessSMS(func, MediaType.APPLICATION_JSON);
            return Response.status(200).entity(psms.buiildRsp(ProcessSMS.CODE.EXCEPTION)).build();
        }
    }

    //------POST JSON---------
    @POST
    @Path("/{param}")       // is function send_sms
    @Produces({MediaType.APPLICATION_JSON + ";charset=utf-8", MediaType.TEXT_HTML})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPostJson(@PathParam("param") String func, String data, @Context HttpServletRequest request) {
        try {
            String ip = Tool.getClientIpAddr(request);
            RequestMessage dataObj = readDataJson(data);
            if (dataObj == null) {
                ProcessSMS psms = new ProcessSMS(func, MediaType.APPLICATION_JSON);
                return Response.status(200).entity(psms.buiildRsp(ProcessSMS.CODE.INVALID_JSON_DATA)).build();
            }
            String host = request.getHeader("host") + "-" + request.getRemoteHost();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("function", func);
            properties.put("host", host);
            properties.put("ip", ip);
            properties.put("user", dataObj.getUser());
            properties.put("pass", dataObj.getPass());
            properties.put("phone", dataObj.getPhone());
            properties.put("brandName", dataObj.getBrandName());
            properties.put("mess", dataObj.getMess());
            properties.put("tranId", dataObj.getTranId());
            properties.put("rqType", MediaType.APPLICATION_JSON);
            // Debug
            if (MyConfig.CONSOLE_OUT) {
                Tool.consoleOut("-------------doPostJson Param Value--------------------");
                Set<String> datakey = properties.keySet();
                for (String paramName : datakey) {
                    String value = properties.get(paramName);
                    properties.put(paramName, value);
                    Tool.consoleOut(paramName + ":" + value);
                }
            }
            //-- Call
            return processRequest(properties);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            ProcessSMS psms = new ProcessSMS(func, MediaType.APPLICATION_JSON);
            return Response.status(200).entity(psms.buiildRsp(ProcessSMS.CODE.EXCEPTION)).build();
        }
    }

    private RequestMessage readDataXML(String xmlInput) {
        RequestMessage data = new RequestMessage();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlInput));
            Document doc = dBuilder.parse(is);
            NodeList nodes = doc.getElementsByTagName("message");
            if (nodes != null && nodes.getLength() > 0) {
                Element oneNote = (Element) nodes.item(0);
                if (oneNote.getElementsByTagName("user").item(0) != null) {
                    String user = oneNote.getElementsByTagName("user").item(0).getTextContent();
                    data.setUser(user);
                }
                if (oneNote.getElementsByTagName("pass").item(0) != null) {
                    String pass = oneNote.getElementsByTagName("pass").item(0).getTextContent();
                    data.setPass(pass);
                }
                if (oneNote.getElementsByTagName("tranId").item(0) != null) {
                    String tranId = oneNote.getElementsByTagName("tranId").item(0).getTextContent();
                    data.setTranId(tranId);
                }
                if (oneNote.getElementsByTagName("brandName").item(0) != null) {
                    String brandName = oneNote.getElementsByTagName("brandName").item(0).getTextContent();
                    data.setBrandName(brandName);       // ID của nhãn => 
                }
                if (oneNote.getElementsByTagName("phone").item(0) != null) {
                    String phone = oneNote.getElementsByTagName("phone").item(0).getTextContent();
                    data.setPhone(phone);       // ID của nhãn => 
                }
                if (oneNote.getElementsByTagName("mess").item(0) != null) {
                    String mess = oneNote.getElementsByTagName("mess").item(0).getTextContent();
                    data.setMess(mess);       // ID của nhãn => 
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NullPointerException e) {
            logger.error("xmlInput:" + xmlInput);
            logger.error(Tool.getLogMessage(e));
        }
        return data;
    }

    private RequestMessage readDataJson(String jsonInput) {
        RequestMessage data = null;
        try {
            data = mapper.readValue(jsonInput, RequestMessage.class);
        } catch (IOException e) {
            logger.error("jsonInput:" + jsonInput);
            logger.error(Tool.getLogMessage(e));
        }
        return data;
    }

}
