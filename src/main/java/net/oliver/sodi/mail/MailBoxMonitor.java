package net.oliver.sodi.mail;

/*
 * Copyright (c) 1996-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.sun.mail.imap.IMAPFolder;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.spring.SodiApplicationListener;
import net.oliver.sodi.util.JsoupUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.blueglacier.email.Attachment;
import tech.blueglacier.email.Email;
import tech.blueglacier.parser.CustomContentHandler;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

/* Monitors given mailbox for new mail */
@Component
public class MailBoxMonitor {

    static final Logger logger = LoggerFactory.getLogger(MailBoxMonitor.class);

    @Autowired
    IInvoiceService invoiceService;

    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    private IMAPFolder folder;

    public void getContent(Message message) throws MessagingException, IOException
    {
        String subject = message.getSubject();
        if(subject.indexOf("New Order")<0)
        {
            logger.info("Not new order,ignore it");
            return;
        }

//        String from = "";
//        ArrayList<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();
        String contentType = message.getContentType();
//        Address[] addresses = message.getFrom();
//        if(addresses.length == 1)
//            from = addresses[0].toString();
//        else
//        {
//            for(int num = 0; num < addresses.length - 1; num++)
//                from += addresses[num].toString() + ", ";
//            from += addresses[addresses.length].toString();
//        }

        if(contentType.contains("TEXT/HTML"))//"TEXT/PLAIN"
        {
            String content = (String) message.getContent();
            Invoice invoice = JsoupUtil.getInvoice(content);
            invoiceService.save(invoice);
            return;
        }
        else if(contentType.contains("multipart"))
        {
            //
            Multipart mp = (Multipart)message.getContent();
            int numParts = mp.getCount();
            for(int count = 0; count < numParts; count++)
            {
                MimeBodyPart part = (MimeBodyPart)mp.getBodyPart(count);
                String content = part.getContent().toString();

//                if(MimeBodyPart.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
//                    attachments.add(part);
//
//                else

                    if(part.getContentType().contains("text/html"))
                {
                    Invoice invoice = JsoupUtil.getInvoice(content);
                    if(invoice!=null)
                    {
                        invoiceService.save(invoice);
                    }else{
                        logger.info("Cant generate invoice from one email..");
                    }

                    return;
                }
//                else
//                    body += content;
            }
        }

    }

    public static void parse(Message msg) throws Exception {
        ContentHandler contentHandler = new CustomContentHandler();

        MimeConfig mime4jParserConfig = MimeConfig.DEFAULT;
        BodyDescriptorBuilder bodyDescriptorBuilder = new DefaultBodyDescriptorBuilder();
        MimeStreamParser mime4jParser = new MimeStreamParser(mime4jParserConfig,DecodeMonitor.SILENT,bodyDescriptorBuilder);
        mime4jParser.setContentDecoding(true);
        mime4jParser.setContentHandler(contentHandler);

        InputStream mailIn = msg.getInputStream();
        mime4jParser.parse(mailIn);

        Email email = ((CustomContentHandler) contentHandler).getEmail();

        List<Attachment> attachments =  email.getAttachments();

        Attachment calendar = email.getCalendarBody();
        Attachment htmlBody = email.getHTMLEmailBody();
        Attachment plainText = email.getPlainTextEmailBody();

        String to = email.getToEmailHeaderValue();
        String cc = email.getCCEmailHeaderValue();
        String from = email.getFromEmailHeaderValue();
        String subject = email.getEmailSubject();

        System.out.println(htmlBody.toString());
    }

    synchronized  boolean updateLastSeenUID(int uid) throws MessagingException {

        // Do not update lastSeenUID if it is larger than the current uid.
        if(StringUtils.isBlank((String)System.getProperties().get("MAILBOX_LAST_SEEN_UID")))
        {
            System.getProperties().setProperty("MAILBOX_LAST_SEEN_UID",String.valueOf(uid));
            return true;
        }
        try {
            int lastSeenUID =Integer.parseInt((String) System.getProperties().get("MAILBOX_LAST_SEEN_UID"));
            if (uid <= lastSeenUID) {
                logger.info("Reutrn false for Check: lastSeenUID : "+lastSeenUID+" uid: "+uid);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception ocurrs in updateLastSeenUID: "+e.getMessage());
        }

        System.getProperties().setProperty("MAILBOX_LAST_SEEN_UID",String.valueOf(uid));
        return true;
    }

    public void connect() throws Exception {
        Properties props = System.getProperties();

        props.setProperty("mail.imaps.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.imaps.socketFactory.fallback", "false");
        props.setProperty("mail.imaps.port", "993");
        props.setProperty("mail.imaps.socketFactory.port", "993");
        props.put("mail.imaps.host", "outlook.office365.com");

        // Get a Session object
        Session session = Session.getInstance(props,null);
        // session.setDebug(true);
        // Get a Store object
//            Store store = session.getStore("imaps");
//            // Connect
////            store.connect("imap.126.com", "oliver_reg", "maiyang9");
//            //DinithraSod1
//            store.connect("sodirentalkarts.com.au", "info@sodirentalkarts.com.au", "HuluGo294");
        Store store = session.getStore("imaps");
        store.connect("outlook.office365.com", 993, "info@sodirentalkarts.com.au", "Maiyang9");

        // Open a Folder
        folder = (IMAPFolder) store.getFolder("INBOX");
        if (folder == null || !folder.exists()) {
            System.out.println("Invalid folder");
//                System.exit(1);
            return;
        }
        if(!folder.isOpen())
        folder.open(Folder.READ_WRITE);
    }

    public void reOpenFolder() throws MessagingException {

        if(!folder.isOpen()){
            Session session = Session.getInstance(System.getProperties(),null);
            Store store = session.getStore("imaps");
            store.connect("outlook.office365.com", 993, "info@sodirentalkarts.com.au", "Maiyang9");
            // Open a Folder
            folder = (IMAPFolder) store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            logger.info("Folder is reopened "+folder.isOpen());
        }
    }
    public  void start() {
//        if (argv.length != 5) {
//            System.out.println(
//                    "Usage: monitor <host> <user> <password> <mbox> <freq>");
//            System.exit(1);
//        }
//        System.out.println("\nTesting monitor\n");


        try {
            connect();
            folder.addMessageCountListener(new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent ev) {
                    Message[] msgs = ev.getMessages();
                    for (int i = 0; i < msgs.length; i++) {
                        try {
                            logger.info("Receive Message " +msgs[i].getMessageNumber() + ":");
//                            if(updateLastSeenUID(msgs[i].getMessageNumber()))
//                            {
                                getContent(msgs[i]);
//                                logger.info("Process Mail done!!!");
//                            }else{
//                                logger.info("Ignore it..");
//                            }

                        } catch (Exception e) {
                            logger.info("Exception Occurs during MessageCounterListener.."+e.getMessage()+" "+e.getClass().getCanonicalName());
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw, true));
                            String str = sw.toString();
                            System.out.println("==========");
                            logger.info(str);
                        }
                    }
                }
            });

            // Check mail once in "freq" MILLIseconds
            int freq = 3000;
            boolean supportsIdle = true;

            for (;;) {
//                Thread.sleep(freq);
//                System.out.println(Thread.currentThread().getName()+" running..");
                logger.info(Thread.currentThread().getName()+" running....");
                    try{
                        if(folder.isOpen())
                        {
                            logger.info("Call idle....");
                            folder.idle();
                        }
//                        else {
//                            reOpenFolder();
//                        }

                    }catch (FolderClosedException ex)
                    {
                        logger.info("FolderClosedException....");
//                        ex.printStackTrace();
                        MailBoxMonitor monitor = SodiApplicationListener.applicationContext.getBean(MailBoxMonitor.class);
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                monitor.start();
                            }
                        });
                        t.setName("SodiMailCheckThread_"+System.currentTimeMillis());
                        t.start();
                        logger.info("New Mail Thread started...."+t.getState());
//                        reOpenFolder();
                        return;
                    }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(Thread.currentThread().getName()+" Exit....");
    }
}