/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.VIVAS;

import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.Md5;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang.StringEscapeUtils;
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
public class SendSMSVivas {

    static Logger logger = Logger.getLogger(SendSMSVivas.class);

    public static enum STATUS {

        SUCCESS(0, "Request được tiếp nhận thành công"),
        BRAND_NOT_VALID(3, "Request bị từ chối vì Brandname không tồn tại hoặc không thuộc sở hữu"),
        WRONG_TEMPLATE(4, "Request bị từ chối vì không tìm thấy template hoặc không đúng template"),
        WRONG_CHECKSUM(5, "Request bị từ chối vì chứa một checksum sai"),
        DUPLICATE_ID(6, "Request bị từ chối vì trùng ID"),
        OVER_QUATA(8, "Request bị từ chối vì vượt hạn mức gửi tin"),
        WRONG_TYPE(9, "Request bị từ thối vì thiếu loại SMS"),
        LOSE_TIME_SEND(10, "Request bị từ chối vì thiếu thời gian gửi"),
        DUPLICATE_MEGID(12, "Request bị từ chối vì trùng msgid"),
        OVER_MSG(13, "Request bị từ chối vì vượt quá số lượng số điện thoại trong request"),
        WRONG_PHONE(14, "Request bị từ chối vì chứa số điện thoại sai"),
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
            if (Tool.checkNull(str)) {
                str = "Unknow VAL:" + val;
            }
            return str;
        }
    }

    /**
     * sendOneQueue Gui 1 tin CSKH - QC
     *
     * @param queue
     * @return
     */
    public static String[] sendOneQueue(SmsBrandQueue queue) {
        //  STATUS - REQID
        String data = buildRequest(queue);
        String response = VivasConfig.excutePost(VivasConfig.URL_SEND_SMS, data);
        MyLog.debug(response);
        String[] result = getResult(response);  // Da Login lai torng nay
        if (result[0].equals(STATUS.NOT_LOGIN.val + "")) {
            // Goi lai vi da login lai trong  getResult(response)
            result = sendOneQueue(queue);
        }
        return result;
    }

//    public static void main(String[] args) {
//        String str = "Tran trong kinh moi: Cac cu, cac Ong, cac Ba, ben Noi, ben Ngoai, con chau dau, chau re trong ho tren Toan Quoc ve du le gio to ho HOANH, NGUYEN HOANH vao thu 7 ngay 31/3/2018 (tuc 15/02 AL).\r\nD/c: 111 duong Hoang Mai, Q.Hoang Mai, TP. Ha Noi.\r\nThay mat truong ho: NGUYEN HOANH THONG - DT: 0947 044 055";
//        System.out.println(StringEscapeUtils.escapeXml(str));
//        System.out.println(validMessage(str));
////                + "XIN CHUC MUNG!";
////        str = validMessage(str);
////        char[] arr = str.toCharArray();
////        for (char c : arr) {
////            System.out.println(c);
////        }
//
////        String sendTime = "20171117150958";
////        String phone = "84917233352";
////        String brand = "FORD.THUDO";
////        String messageId = "84917233352-3b72f936c9aa7f";
////        String message = "CT khuyen mai cuc HOT: Giam 15% dau & loc dau. Tang The suachua 300.000? cho H? &gt;3 trieu. Tg: tu 17/11-31/12. Dat hen truoc 24h de nhan quatang.Lh: 02436811111";
////        String strchecksum = "username=" + VivasConfig.USER
////                + "&password=" + VivasConfig.SHA1_HASH
////                + "&brandname=" + brand
////                + "&sendtime=" + sendTime
////                + "&msgid=" + messageId // Sai cho Nay roi da fix
////                + "&msg=" + message // De nguyen vi no chi la 1 Dau &
////                + "&msisdn=" + phone
////                + "&sharekey=" + VivasConfig.SHARE_KEY;
////        System.out.println(strchecksum);
////        System.out.println(Md5.encryptMD5(strchecksum));
//
//        
//        
////        String strRequest = "<RQST><REQID>84917233352-3b72f936c9aa7f</REQID><BRANDNAME>FORD.THUDO</BRANDNAME><TEXTMSG>CT khuyen mai cuc HOT: Giam 15% dau &amp; loc dau. Tang The suachua 300.000? cho H? &amp;gt;3 trieu. Tg: tu 17/11-31/12. Dat hen truoc 24h de nhan quatang.Lh: 02436811111</TEXTMSG><SENDTIME>20171117150958</SENDTIME><TYPE>1</TYPE><DESTINATION><MSGID>84917233352-16733438318975</MSGID><MSISDN>84917233352</MSISDN><CHECKSUM>4b0fe962990847d91367d468c9fa25bb</CHECKSUM></DESTINATION></RQST>";
////        String strRequest = "<RQST><REQID>84917233352-3b72f936c9aa7f</REQID><BRANDNAME>FORD.THUDO</BRANDNAME><TEXTMSG>CT khuyen mai cuc HOT: Giam 15% dau &amp; loc dau. Tang The suachua 300.000? cho H? &amp;gt;3 trieu. Tg: tu 17/11-31/12. Dat hen truoc 24h de nhan quatang.Lh: 02436811111</TEXTMSG><SENDTIME>20171117150958</SENDTIME><TYPE>1</TYPE><DESTINATION><MSGID>84917233352-16733438318975</MSGID><MSISDN>84917233352</MSISDN><CHECKSUM>3d37a0562c839e10e211aeecd8780942</CHECKSUM></DESTINATION></RQST>";
//        
////        String response = VivasConfig.excutePost(VivasConfig.URL_SEND_SMS, strRequest);
////        System.out.println(response);
//    }

    private static String buildRequest(SmsBrandQueue queue) {

        int type = -1;
        if (queue.getType() == BrandLabel.TYPE.CSKH.val) {
            type = VivasConfig.TYPE.CSKH.val;
        }
        if (queue.getType() == BrandLabel.TYPE.QC.val) {
            type = VivasConfig.TYPE.QC.val;
        }
        // TODO
        long messageId = System.nanoTime() / 1000;
        queue.setMessageId(queue.getPhone() + "-" + messageId);                 // Do Minh sinh ra
        String sendTime = DateProc.createYYYYMMDDhh24miss();

        String strchecksum = "username=" + VivasConfig.USER
                + "&password=" + VivasConfig.SHA1_HASH
                + "&brandname=" + queue.getLabel()
                + "&sendtime=" + sendTime
                + "&msgid=" + queue.getMessageId() // Sai cho Nay roi da fix
                + "&msg=" + queue.getMessage() // De nguyen vi no chi la 1 Dau &
                + "&msisdn=" + queue.getPhone()
                + "&sharekey=" + VivasConfig.SHARE_KEY;

//        String messageXML = StringEscapeUtils.escapeXml(queue.getMessage());                                        // Valid ky tu thay the cho XML vi khi request ve no chi la ky tu dac biet
        String messageXML = validMessage(queue.getMessage());        // Valid ky tu thay the cho XML vi khi request ve no chi la ky tu dac biet

        String result = "<RQST>"
                + "<REQID>" + queue.getSystemId() + "</REQID>"
                + "<BRANDNAME>" + queue.getLabel() + "</BRANDNAME>"
                + "<TEXTMSG>" + messageXML + "</TEXTMSG>" // Tin Khach Hang gui den nhung da duoc Valid
                + "<SENDTIME>" + sendTime + "</SENDTIME>"
                + "<TYPE>" + type + "</TYPE>"
                + "<DESTINATION>"
                + "<MSGID>" + queue.getMessageId() + "</MSGID>"
                + "<MSISDN>" + queue.getPhone() + "</MSISDN>"
                + "<CHECKSUM>" + Md5.encryptMD5(strchecksum) + "</CHECKSUM>"
                + "</DESTINATION>"
                + "</RQST>";
        // Set vao day de cho lay ket qua ve
        MyLog.debug("REQUEST VIVAS strchecksum:" + strchecksum);
        MyLog.debug("REQUEST VIVAS:" + result);
        queue.setReqId_vivas(queue.getSystemId());
        return result;
    }

    private static String validMessage(String msg) {
        msg = msg.replaceAll("&", "&amp;");
        msg = msg.replaceAll("<", "&lt;");
        msg = msg.replaceAll(">", "&gt;");
        msg = msg.replaceAll("'", "&apos;");
        msg = msg.replaceAll("\"", "&quot;");
        msg = msg.replaceAll("\r", "&#13;");
        msg = msg.replaceAll("\n", "&#10;");
        return msg;
    }

    private static String[] getResult(String xmlInput) {
        String[] result = {Login.STATUS.PARSE_RESULT_ERROR.val + "", "0"};
//        Tool.debug("Response Result:" + xmlInput);
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlInput));
            Document doc = dBuilder.parse(is);
            NodeList nodes = doc.getElementsByTagName("RPLY");
            if (nodes != null && nodes.getLength() > 0) {
                Element oneNote = (Element) nodes.item(0);
                String status = Login.STATUS.PARSE_RESULT_ERROR.val + "";
                String reqId = "0";
                if (oneNote.getElementsByTagName("STATUS").item(0) != null) {
                    status = oneNote.getElementsByTagName("STATUS").item(0).getTextContent();
                }
                if (status.equals(STATUS.NOT_LOGIN.val + "")) {
                    // Chua dang nhap dang nhap lai
                    Login.loginRequest();
                } else if (oneNote.getElementsByTagName("REQID") != null && oneNote.getElementsByTagName("REQID").getLength() > 0) {
                    reqId = oneNote.getElementsByTagName("REQID").item(0).getTextContent();
                }
                result[0] = status;
                result[1] = reqId;
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NullPointerException e) {
            logger.error("xmlInput:" + xmlInput);
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }
}
