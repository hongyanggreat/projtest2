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
public class SendSMSExtVivas {

//    static Logger logger = Logger.getLogger(SendSMSExtVivas.class);
//    public static enum STATUS {
//
//        SUCCESS(0, "Request được tiếp nhận thành công"),
//        BRAND_NOT_VALID(3, "Request bị từ chối vì Brandname không tồn tại hoặc không thuộc sở hữu"),
//        WRONG_TEMPLATE(4, "Request bị từ chối vì không tìm thấy template hoặc không đúng template"),
//        WRONG_CHECKSUM(5, "Request bị từ chối vì chứa một checksum sai"),
//        DUPLICATE_ID(6, "Request bị từ chối vì trùng ID"),
//        OVER_QUATA(8, "Request bị từ chối vì vượt hạn mức gửi tin"),
//        WRONG_TYPE(9, "Request bị từ thối vì thiếu loại SMS"),
//        LOSE_TIME_SEND(10, "Request bị từ chối vì thiếu thời gian gửi"),
//        DUPLICATE_MEGID(12, "Request bị từ chối vì trùng msgid"),
//        OVER_MSG(13, "Request bị từ chối vì vượt quá số lượng số điện thoại trong request"),
//        WRONG_PHONE(14, "Request bị từ chối vì chứa số điện thoại sai"),
//        NOT_LOGIN(20, "Request bị từ chối vì chưa đăng nhập hoặc mất session"),
//        OVER_TPS(21, "Request bị từ chối vì quá số lượng request đồng thời đến hệ thống"),
//        ERROR_50(50, "Lỗi xử lý"),
//        ERROR_51(51, "Lỗi xử lý"),
//        ERROR_52(52, "Lỗi xử lý"),
//        WRONG_PROTOCOL(98, "Lỗi sai protocol gọi request"),
//        LOSE_PARAM(99, "Lỗi thiếu tham số gọi request"), //==
//        PARSE_RESULT_ERROR(100, "Lỗi Parse ket qua tra ve"), //==
//        UNKNOW_ERROR(101, "Unknow Error"), //==
//        ;
//        public int val;
//        public String mess;
//
//        private STATUS(int val, String mess) {
//            this.val = val;
//            this.mess = mess;
//        }
//
//        public static String getmessage(int val) {
//            String str = "";
//            STATUS[] tmp = STATUS.values();
//            for (STATUS one : tmp) {
//                if (one.val == val) {
//                    str = one.mess;
//                }
//            }
//            return str;
//        }
//    }
//    public static String[] sendMultiQueue(GroupBrandQueue queue) {
//        //  STATUS - REQID
//        String data = buildRequest(queue);
//        String response = VivasConfig.excutePost(VivasConfig.URL_SEND_SMS_EXT, data);
//        String[] result = getResult(response);
//        return result;
//    }
//
//    private static String buildRequest(GroupBrandQueue gQueue) {
//        UUID idOne = UUID.randomUUID();
//        int type = -1;
//        String result = "";
//        if (gQueue != null && !gQueue.getgQueue().isEmpty()) {
//            if (gQueue.getType() == BrandLabel.TYPE.CSKH.val) {
//                type = VivasConfig.TYPE.CSKH.val;
//            }
//            if (gQueue.getType() == BrandLabel.TYPE.QC.val) {
//                type = VivasConfig.TYPE.QC.val;
//            }
//            String sendTime = DateProc.createYYYYMMDDhh24miss();
//            ArrayList<SmsBrandQueue> listQueue = gQueue.getgQueue();
//            result = "<RQST>"
//                    + "  <REQID>" + idOne + "</REQID>"
//                    + "  <BRANDNAME>" + gQueue.getLabel() + "</BRANDNAME>"
//                    + "  <TEXTMSG>" + gQueue.getMessage() + "</TEXTMSG>"
//                    + "  <SENDTIME>" + sendTime + "</SENDTIME>"
//                    + "  <TYPE>" + type + "</TYPE>"
//                    + "     <DESTINATIONS>"
//                    + "         // loop";
//            while (!listQueue.isEmpty()) {
//                SmsBrandQueue queue = listQueue.remove(0);
//                String checksum = "username=" + VivasConfig.USER + "&password=" + VivasConfig.SHA1_HASH + "&brandname=" + queue.getLabel() + "&sendtime=" + sendTime + "&msgid=" + queue.getId() + "&msg=" + queue.getMessage() + "&msisdn=" + queue.getPhone() + "&sharekey=" + VivasConfig.SHARE_KEY;
//                result += "             <DESTINATION>"
//                        + "                 <MSGID>" + queue.getId() + "</MSGID>"
//                        + "                 <MSISDN>" + queue.getPhone() + "</MSISDN>"
//                        + "                 <CHECKSUM>" + Md5.encryptMD5(checksum) + "</CHECKSUM>  "
//                        + "             </DESTINATION>";
//            }
//            result += "         // end loop"
//                    + "     </DESTINATIONS>"
//                    + "</RQST>";
//        }
//        return result;
//    }
//    private static String[] getResult(String xmlInput) {
//        String[] result = {Login.STATUS.PARSE_RESULT_ERROR.val + "", "0"};
//
//        try {
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            InputSource is = new InputSource();
//            is.setCharacterStream(new StringReader(xmlInput));
//            Document doc = dBuilder.parse(is);
//            NodeList nodes = doc.getElementsByTagName("RPLY");
//            if (nodes != null) {
//                Element oneNote = (Element) nodes.item(0);
//                String status = Login.STATUS.PARSE_RESULT_ERROR.val + "";
//                String reqId = "0";
//                if (oneNote.getElementsByTagName("STATUS").item(0) != null) {
//                    status = oneNote.getElementsByTagName("STATUS").item(0).getTextContent();
//                }
//                if (oneNote.getElementsByTagName("REQID") != null) {
//                    reqId = oneNote.getElementsByTagName("REQID").item(0).getTextContent();
//                }
//                result[0] = status;
//                result[1] = reqId;
//            }
//        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
//            logger.error("xmlInput:" + xmlInput);
//            logger.error(Tool.getLogMessage(e));
//        }
//        return result;
//    }
}
