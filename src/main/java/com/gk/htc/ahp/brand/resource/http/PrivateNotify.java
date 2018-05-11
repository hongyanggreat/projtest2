/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.resource.http;

import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.Account;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.sendMT.service.ProcessSMS;
import java.util.HashMap;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
@Path("/private")
public class PrivateNotify {

    static final Logger logger = Logger.getLogger(ServiceBrand.class);
    private static final String[] funtion = {"loadAcc", "reloadBrand"};
    String[] ipAllow = {"127.0.0.1"};

    public boolean validIP(String ipRequest) {
        boolean flag = false;
        try {
            for (String one : ipAllow) {
                if (ipRequest.equals(one.trim())) {
                    flag = true;
                    break;
                }
            }
        } catch (Exception e) {
        }
        return flag;
    }

    private Response processRequest(HashMap<String, String> properties, String ip) {
        try {
            Tool.debug("|=>Start PrivateNotify FROM IP: " + ip);
            MyLog.debug("|=>Start PrivateNotify FROM IP: " + ip);
            Account accDao = new Account();
            BrandLabel brDao = new BrandLabel();
            if (!validIP(ip)) {
                Tool.debug("Request PrivateNotify IP not Valid: " + ip);
                return Response.status(Response.Status.OK.getStatusCode()).entity(ProcessSMS.CODE.IP_SER_NOT_ALLOW.mess).build();
            }
            String function = Tool.getString(properties.get("function"));
            if (!validFunction(function)) {
                Tool.debug("Request PrivateNotify UNKNOW_SERVICE From IP: " + ip);
                return Response.status(Response.Status.OK.getStatusCode()).entity(ProcessSMS.CODE.UNKNOW_SERVICE.mess).build();
            }
            Tool.debug(function + "=>");
            if (function.equalsIgnoreCase("loadAcc")) {
                String action = Tool.getString(properties.get("action"));
                if (action.equals("add")) {
                    String user = Tool.getString(properties.get("user"));
                    Account acc = accDao.getByUser(user);
                    Account.reload(acc);
                    Tool.debug(action + "|user=" + user);
                }
                if (action.equals("update")) {
                    int id = Tool.getInt(properties.get("id"));
                    Account acc = accDao.getById(id);
                    if (acc != null) {
                        Account.reload(acc);
                        Tool.debug(action + "|user=" + acc.getUserName());
                    }
                }
                if (action.equals("del")) {
                    int id = Tool.getInt(properties.get("id"));
                    Account acc = accDao.getById(id);
                    if (acc != null) {
                        Account.remove(acc);
                        Tool.debug(action + "|user=" + acc.getUserName());
                    }
                }
            }
            if (function.equals("reloadBrand")) {
                String action = Tool.getString(properties.get("action"));
                if (action.equals("add")) {
                    String label = Tool.getString(properties.get("label"));
                    String user = Tool.getString(properties.get("user"));
                    BrandLabel br = brDao.getByRIdLabel(user, label);
                    if (br != null) {
                        BrandLabel.reload(br);
                        Tool.debug(action + "|label=" + br.getBrandLabel() + "|userOwner=" + br.getUserOwner());
                    }
                }
                if (action.equals("update")) {
                    int id = Tool.getInt(properties.get("id"));
                    BrandLabel br = brDao.getById(id);
                    if (br != null) {
                        BrandLabel.reload(br);
                        Tool.debug(action + "|label=" + br.getBrandLabel() + "|userOwner=" + br.getUserOwner());
                    }
                }
                if (action.equals("del")) {
                    int id = Tool.getInt(properties.get("id"));
                    BrandLabel br = brDao.getById(id);
                    if (br != null) {
                        BrandLabel.reMove(br);
                        Tool.debug(action + "|label=" + br.getBrandLabel() + "|userOwner=" + br.getUserOwner());
                    }
                }
            }

            return Response.status(Response.Status.OK.getStatusCode()).entity(ProcessSMS.CODE.SUCCESS.mess).build();
            // -----------
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            Tool.debug("Nhan ServiceMO EXCEPTION");
            return Response.status(Response.Status.OK.getStatusCode()).entity(ProcessSMS.CODE.EXCEPTION.mess).build();
        }
    }

    //---------------
    @GET
    @Path("/{param}")
    public Response doGet(@PathParam("param") String path,
            @Context UriInfo uriInfo, @Context HttpServletRequest request) {
        try {
            String ip = request.getRemoteAddr();
            MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("function", path);
            Set<String> key = queryParams.keySet();
            for (String name : key) {
                String value = queryParams.getFirst(name);
                properties.put(name, value);
            }
            return processRequest(properties, ip);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            return Response.status(Response.Status.OK.getStatusCode()).entity(ProcessSMS.CODE.EXCEPTION.mess).build();
        }
    }

    @POST
    @Path("/{param}")
    public Response doPost(@PathParam("param") String path,
            @Context UriInfo uriInfo, @Context HttpServletRequest request) {
        try {
            String ip = request.getRemoteAddr();
            MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("function", path);
            Set<String> key = queryParams.keySet();
            for (String name : key) {
                String value = queryParams.getFirst(name);
                properties.put(name, value);
            }
            return processRequest(properties, ip);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            return Response.status(200).entity(ProcessSMS.CODE.EXCEPTION.mess).build();
        }
    }

    private boolean validFunction(String func) {
        boolean result = Boolean.FALSE;
        for (String onefunc : funtion) {
            if (onefunc.equalsIgnoreCase(func)) {
                result = Boolean.TRUE;
                break;
            }
        }
        return result;
    }
}
