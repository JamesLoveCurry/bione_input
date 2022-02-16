package com.yusys.bione.comp.utils;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
 
    private static String PORT = null;
 
    private static String SERVER = null;//邮件服务器mail.cpip.net.cn
 
    private static String FROM = "廊坊银行数据补录平台";//发送者,显示的发件人名字
 
    private static String USER = null;//发送者邮箱地址
 
    private static String PASSWORD = null;//密码
    
    
    static{
//    	PropertiesUtils pu = PropertiesUtils.get("email.properties");
//    	
//    	USER = pu.getProperty("mail.username");
//    	
//    	PASSWORD = pu.getProperty("mail.password");
//    	
//    	SERVER = pu.getProperty("mail.host");
//    	
//    	PORT = pu.getProperty("mail.port");
    }
    
    
    public static void sendEmail(String sendFrom, String title,String body) throws UnsupportedEncodingException {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SERVER);
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.auth", "true");
            Transport transport = null;
            Session session = Session.getDefaultInstance(props, null);
            transport = session.getTransport("smtp");
            transport.connect(SERVER, USER, PASSWORD);
            MimeMessage msg = new MimeMessage(session);
            msg.setSentDate(new Date());
            InternetAddress fromAddress = new InternetAddress(USER,FROM,"UTF-8");
            msg.setFrom(fromAddress);
            InternetAddress[] toAddress = new InternetAddress[1];
            toAddress[0] = new InternetAddress(sendFrom);
            msg.setRecipients(Message.RecipientType.TO, toAddress);
            msg.setSubject(title, "UTF-8");   
            msg.setText(body, "UTF-8");
            msg.saveChanges();
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}