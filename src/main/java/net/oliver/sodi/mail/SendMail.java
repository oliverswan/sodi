package net.oliver.sodi.mail;

import net.oliver.sodi.controller.InvoiceController;
import net.oliver.sodi.model.Backorder;
import net.oliver.sodi.model.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

@Component
public class SendMail {

    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    /**
     * Message对象将存储我们实际发送的电子邮件信息，
     * Message对象被作为一个MimeMessage对象来创建并且需要知道应当选择哪一个JavaMail session。
     */
    private MimeMessage message;
    private Session session;
    private Transport transport;

    private String mailHost = "";
    private String sender_username = "";
    private String sender_password = "";

    private Properties properties = new Properties();

    static final Logger logger = LoggerFactory.getLogger(SendMail.class);
    /*
     * 初始化方法
     */
    public SendMail() {
        try {

            properties.setProperty("mail.smtp.host", "smtp.gmail.com");  // 设置邮件服务器主机名 smtp.office365.com smtp.office365.com
            properties.setProperty("mail.transport.protocol", "smtp" );    // 发送邮件协议名称
            properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            properties.setProperty("mail.smtp.port", "587");
            properties.setProperty("mail.smtp.socketFactory.port","465");//587
            properties.setProperty("mail.sender.username", "sodikartsau@gmail.com");    // 发送邮件地址 info@sodirentalkarts.com.au
            properties.setProperty("mail.sender.password", "Zar37097");    // 发送邮件地址授权码 Niceday990

            properties.setProperty("mail.debug", "true");  // 开启debug调试
            properties.setProperty("mail.smtp.auth", "true");   // 发送服务器需要身份验证
            properties.setProperty("mail.smtp.starttls.enable", "true");

            this.mailHost = properties.getProperty("mail.smtp.host");
            this.sender_username = properties.getProperty("mail.sender.username");
            this.sender_password = properties.getProperty("mail.sender.password");


        } catch (Exception e) {
            e.printStackTrace();
        }

//        session = Session.getInstance(properties);
//        session.setDebug(false);// 开启后有调试信息
        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("sodikartsau@gmail.com",
                        "Zar37097");
            }
        });
        message = new MimeMessage(session);
    }

    public void doSendBackOrderRemindEmail(Invoice invoice, Backorder bo, String receiveUser) {
        try {
            // 发件人
            InternetAddress from = new InternetAddress(sender_username);
            message.setFrom(from);

            // 收件人
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);

            // 邮件主题
            message.setSubject("BackOrder Remind For Invoice ["+invoice.getInvoiceNumber()+"]");

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();

            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();

            StringBuffer sb = new StringBuffer();
            sb.append("Dear Customer,").append("<br><br>").append("Thank you for the order you placed. Unfortunately, the following item(s) you've ordered are currently not available and have been placed on back order.<br><br>");
            for(Iterator<Map.Entry<String,Integer>> iter = bo.getOrders().entrySet().iterator();iter.hasNext();)
            {
                Map.Entry<String,Integer> entry = iter.next();
                sb.append(entry.getValue()+" X "+ entry.getKey()).append("<br>");
            }
            sb.append("<br>");
            sb.append("These items are on order with Sodi and we'll ship them  as soon as possible.");
            sb.append("If you have any questions, please contact us.<br>");
            sb.append("We appreciate your business and we apologize for any inconvenience this delay causes you.<br><br>");
            sb.append("Kind regards<br>");
            sb.append("Sodi Karts Australasia");
            contentPart.setContent(sb.toString(), "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);
            message.setContent(multipart);

            Transport.send(message);
            // 保存邮件
            /*// message.saveChanges();
            transport = session.getTransport("smtp");
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(mailHost, sender_username, sender_password);
            //            // 发送
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();*/
            logger.info("Successfully send backorder Email to: "+receiveUser);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doSendHtmlEmail(String subject, String sendHtml, String receiveUser, ByteArrayOutputStream os) {
        try {
            // 发件人
            InternetAddress from = new InternetAddress(sender_username);
            message.setFrom(from);

            // 收件人
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);

            // 邮件主题
            message.setSubject(subject);

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();

            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(sendHtml, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);

            // 添加附件的内容
//            if (attachment != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
//                DataSource source = new FileDataSource(attachment);

                byte[] bytes = os.toByteArray();
                DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
                attachmentBodyPart.setDataHandler(new DataHandler(dataSource));

                attachmentBodyPart.setFileName("invoice.pdf");
                multipart.addBodyPart(attachmentBodyPart);
//            }
            // 将multipart对象放到message中
            message.setContent(multipart);
            // 保存邮件
            // message.saveChanges();
            transport = session.getTransport("smtp");
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(mailHost, sender_username, sender_password);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("send success!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
       /* SendMail se = new SendMail(true);
        File pdf = new File("d:\\aaa.pdf");
        try {
            Out input  = new FileInputStream(pdf);
            ByteArrayOutputStream bos =
            se.doSendHtmlEmail("title", "", "oliver_daily@126.com", );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

}
