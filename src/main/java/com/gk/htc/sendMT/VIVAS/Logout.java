/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.VIVAS;

import com.gk.htc.ahp.brand.common.Tool;
import java.io.IOException;
import java.io.StringReader;
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

/**
 *
 * @author TUANPLA
 */
public class Logout {

    static Logger logger = Logger.getLogger(Logout.class);

    public static enum STATUS {

        SUCCESS(0, "Đăng xuất thành công"),
        NOT_LOGIN(20, "Request bị từ chối vì chưa đăng nhập hoặc mất session"),
        OVER_TPS(21, "Request bị từ chối vì quá số lượng request đồng thời đến hệ thống"),
        ERROR_50(50, "Lỗi xử lý"),
        ERROR_51(51, "Lỗi xử lý"),
        ERROR_52(52, "Lỗi xử lý"),
        WRONG_PROTOCOL(98, "Lỗi sai protocol gọi request"),
        LOSE_PARAM(99, "Lỗi thiếu tham số gọi request"), //==
        PARSE_RESULT_ERROR(100, "Lỗi Parse ket qua tra ve"), //==
        UNKNOW_ERROR(101, "Unknow Error"), //==
        ;
        public int val;
        public String mess;

        private STATUS(int val, String mess) {
            this.val = val;
            this.mess = mess;
        }

        public static String getmessage(int val) {
            String str = "";
            STATUS[] tmp = STATUS.values();
            for (STATUS one : tmp) {
                if (one.val == val) {
                    str = one.mess;
                }
            }
            return str;
        }
    }

    public static String[] logOut() {
        //  STATUS - REQID
        String response = VivasConfig.excutePost(VivasConfig.URL_LOG_OUT, "");
        String[] result = getResult(response);
        return result;
    }

    private static String[] getResult(String xmlInput) {
        String[] result = {Login.STATUS.PARSE_RESULT_ERROR.val + "", "0"};

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlInput));
            Document doc = dBuilder.parse(is);
            NodeList nodes = doc.getElementsByTagName("RPLY");
            if (nodes != null) {
                Element oneNote = (Element) nodes.item(0);
                String status = Login.STATUS.PARSE_RESULT_ERROR.val + "";
                String reqId = "0";
                if (oneNote.getElementsByTagName("STATUS").item(0) != null) {
                    status = oneNote.getElementsByTagName("STATUS").item(0).getTextContent();
                }
                if (oneNote.getElementsByTagName("REQID") != null) {
                    reqId = oneNote.getElementsByTagName("REQID").item(0).getTextContent();
                }
                result[0] = status;
                result[1] = reqId;
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            logger.error("xmlInput:" + xmlInput);
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }
}
