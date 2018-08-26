package net.oliver.sodi.mail;

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

    /*
     * 初始化方法
     */
    public SendMail() {
        try {
            properties.setProperty("mail.debug", "true");  // 开启debug调试
            properties.setProperty("mail.smtp.auth", "true");   // 发送服务器需要身份验证
            properties.setProperty("mail.smtp.host", "outlook.office365.com");  // 设置邮件服务器主机名
            properties.setProperty("mail.transport.protocol", "smtp" );    // 发送邮件协议名称
            properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            properties.setProperty("mail.smtp.port", "587");
            properties.setProperty("mail.smtp.socketFactory.port","587");
            properties.setProperty("mail.sender.username", "info@sodirentalkarts.com.au");    // 发送邮件地址
            properties.setProperty("mail.sender.password", "Maiyang9");    // 发送邮件地址授权码

            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");

            this.mailHost = properties.getProperty("mail.smtp.host");
            this.sender_username = properties.getProperty("mail.sender.username");
            this.sender_password = properties.getProperty("mail.sender.password");


        } catch (Exception e) {
            e.printStackTrace();
        }

        session = Session.getInstance(properties);
        session.setDebug(false);// 开启后有调试信息
        message = new MimeMessage(session);
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
