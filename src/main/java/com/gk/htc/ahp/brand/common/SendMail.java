/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.common;

import com.gk.htc.ahp.brand.entity.Account;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class SendMail {

    static final Logger logger = Logger.getLogger(SendMail.class);
    public static String[] toEmail = {"tuanpla@ahp.vn", "hieuhd@ahp.vn", "ducta@ahp.vn"};

    public static boolean sendMail(String subject, String content, String fromName) {
        boolean flag = false;
        try {
            Properties props = new Properties();
//          String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            props.put("mail.smtp.host", MyConfig.MAIL_HOST);
            props.put("mail.smtp.port", "25");
            props.put("mail.debug", MyConfig.MAIL_DEBUG);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.socketFactory.port", "25");
//            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            // Get the default Session object.
            Session session = Session.getInstance(props);
            // Create a default MimeMessage object.
            MimeMessage messageSend = new MimeMessage(session);
            // Set the RFC 822 "From" header field using the
            // value of the InternetAddress.getLocalAddress method.
            messageSend.setFrom(new InternetAddress(MyConfig.SMTP_MAIL, fromName));

            Address[] addresses = new Address[toEmail.length];
            for (int i = 0; i < toEmail.length; i++) {
                Address address = new InternetAddress(toEmail[i]);
                addresses[i] = address;
                // Add the given addresses to the specified recipient type.
                messageSend.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail[i]));
            }
            // Set the "Subject" header field.
            messageSend.setSubject(subject, "utf-8");
            // Sets the given String as this part's content,
            // with a MIME type of "text/plain".
            Multipart mp = new MimeMultipart("alternative");
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setContent(content, "text/html;charset=utf-8");
            mp.addBodyPart(mbp);
            messageSend.setContent(mp);
            messageSend.saveChanges();
            // Send message
            Transport transport = session.getTransport("smtp");
//            transport.connect(MyConfig.MAIL_HOST, MyConfig.SMTP_MAIL, MyConfig.SMTP_PASS);
//            Tool.debug(MyConfig.SMTP_MAIL);
//            Tool.debug(MyConfig.SMTP_PASS);
//            Tool.debug(MyConfig.MAIL_HOST);
            transport.connect(MyConfig.SMTP_MAIL, MyConfig.SMTP_PASS);
            transport.sendMessage(messageSend, addresses);
            transport.close();
            flag = true;
        } catch (UnsupportedEncodingException | MessagingException e) {
            logger.error(Tool.getLogMessage(e));
        }
        return flag;
    }

    public static boolean sendMailTo(String email, String subject, String content, String fromName) {
        boolean flag = false;
        try {
            Properties props = new Properties();
//          String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            props.put("mail.smtp.host", MyConfig.MAIL_HOST);
            props.put("mail.smtp.port", "25");
            props.put("mail.debug", MyConfig.MAIL_DEBUG);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.socketFactory.port", "25");
//            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            // Get the default Session object.
            Session session = Session.getInstance(props);
            // Create a default MimeMessage object.
            MimeMessage messageSend = new MimeMessage(session);
            // Set the RFC 822 "From" header field using the
            // value of the InternetAddress.getLocalAddress method.
            messageSend.setFrom(new InternetAddress(MyConfig.SMTP_MAIL, fromName));

            Address[] addresses = new Address[1];

            Address address = new InternetAddress(email);
            addresses[0] = address;
            // Add the given addresses to the specified recipient type.
            messageSend.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            // Set the "Subject" header field.
            messageSend.setSubject(subject, "utf-8");
            // Sets the given String as this part's content,
            // with a MIME type of "text/plain".
            Multipart mp = new MimeMultipart("alternative");
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setContent(content, "text/html;charset=utf-8");
            mp.addBodyPart(mbp);
            messageSend.setContent(mp);
            messageSend.saveChanges();
            // Send message
            Transport transport = session.getTransport("smtp");
//            transport.connect(MyConfig.MAIL_HOST, MyConfig.SMTP_MAIL, MyConfig.SMTP_PASS);
            Tool.debug(MyConfig.SMTP_MAIL);
            Tool.debug(MyConfig.SMTP_PASS);
            Tool.debug(MyConfig.MAIL_HOST);
            transport.connect(MyConfig.SMTP_MAIL, MyConfig.SMTP_PASS);
            transport.sendMessage(messageSend, addresses);
            transport.close();
            flag = true;
        } catch (UnsupportedEncodingException | MessagingException e) {
            logger.error(Tool.getLogMessage(e));
        }
        return flag;
    }
}
