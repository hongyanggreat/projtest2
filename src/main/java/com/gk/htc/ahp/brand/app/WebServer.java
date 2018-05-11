package com.gk.htc.ahp.brand.app;

import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.sendMT.service.ProcessSMS;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public final class WebServer {

    final Logger logger = Logger.getLogger(WebServer.class);
    Server server;
    private String name;

    public WebServer() {
        this.name = "WebServer[" + DateProc.createTimestamp() + "]";
        MonitorWorker.addDemonName(this.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void start() {
        logger.setLevel(Level.INFO);
        try {

            ServletHolder sh = new ServletHolder(ServletContainer.class);
            sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
            sh.setInitParameter("com.sun.jersey.config.property.packages", "com.gk.htc.ahp.brand.resource.http");
            sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
            sh.setInitOrder(1);
            server = new Server(MyConfig.web_port);
            Context context = new Context(server, MyConfig.contextPath, Context.REQUEST);
            context.addServlet(sh, "/*");
            MyLog.debug("Start Restful Web Server: contextPath: " + MyConfig.contextPath + " | Port: " + MyConfig.web_port);
            Tool.debug("Start Restful Web Server: contextPath: " + MyConfig.contextPath + " | Port: " + MyConfig.web_port);
            //---
            server.start();
        } catch (Exception ex) {
            logger.error(ex.getStackTrace());
        }
    }

    public void stop() {
        try {
            server.stop();
            ProcessSMS.shutDownTraffic();
            MonitorWorker.removeDemonName(this.getName());
            MyLog.debug("STOP Restful Web Server: contextPath: " + MyConfig.contextPath + " | Port: " + MyConfig.web_port);
            Tool.debug("STOP Restful Web Server: contextPath: " + MyConfig.contextPath + " | Port: " + MyConfig.web_port);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }

    }
}
