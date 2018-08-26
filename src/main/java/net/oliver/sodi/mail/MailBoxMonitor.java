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
import net.oliver.sodi.util.JsoupUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/* Monitors given mailbox for new mail */
@Component
public class MailBoxMonitor {

    @Autowired
    IInvoiceService invoiceService;

    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    public void getContent(Message message) throws MessagingException, IOException
    {
        String subject = message.getSubject();
        if(subject.indexOf("New Order")<0)
        {
            return;
        }

        String from = "";
        ArrayList<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();
        String contentType = message.getContentType();
        Address[] addresses = message.getFrom();
        if(addresses.length == 1)
            from = addresses[0].toString();
        else
        {
            for(int num = 0; num < addresses.length - 1; num++)
                from += addresses[num].toString() + ", ";
            from += addresses[addresses.length].toString();
        }

        System.out.println(from);

        if(contentType.contains("TEXT/PLAIN"))
        {
//            Object content = message.getContent();
//            if(content != null)
//                body += content.toString();
        }
        else if(contentType.contains("TEXT/HTML"))
        {
            Object content = message.getContent();
            System.out.println(content);
//            if(content != null)
//                body += Jsoup.parse((String)content).text();
        }
        else if(contentType.contains("multipart"))
        {
            Multipart mp = (Multipart)message.getContent();
            int numParts = mp.getCount();
//            for(int count = 1; count < numParts; count++)
//            {
                MimeBodyPart part = (MimeBodyPart)mp.getBodyPart(1);
                String content = part.getContent().toString();

//                if(MimeBodyPart.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
//                    attachments.add(part);
//
//                else

                    if(part.getContentType().contains("text/html"))
                {
                    Invoice invoice = JsoupUtil.getInvoice(content);
                    invoiceService.save(invoice);
//                }
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
                return false;
            }
        } catch (Exception ignored) { }

        System.getProperties().setProperty("MAILBOX_LAST_SEEN_UID",String.valueOf(uid));
        return true;
    }

    public  void start() {
//        if (argv.length != 5) {
//            System.out.println(
//                    "Usage: monitor <host> <user> <password> <mbox> <freq>");
//            System.exit(1);
//        }
//        System.out.println("\nTesting monitor\n");
        Folder folder = null;

        try {
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
                System.exit(1);
            }


            folder.open(Folder.READ_WRITE);
            folder.addMessageCountListener(new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent ev) {
                    Message[] msgs = ev.getMessages();
                    for (int i = 0; i < msgs.length; i++) {
                        try {
                            System.out.println(Thread.currentThread().getName()+" ： Receive Message " +msgs[i].getMessageNumber() + ":");
                            if(updateLastSeenUID(msgs[i].getMessageNumber()))
                            {
                                getContent(msgs[i]);
                                System.out.println("Process Mail done!!!");
                            }else{
                                System.out.println("Ignore one message..");
                            }

                        } catch (IOException ioex) {
                            ioex.printStackTrace();
                        } catch (MessagingException mex) {
                            mex.printStackTrace();
                        }
                    }
                }
            });

            // Check mail once in "freq" MILLIseconds
            int freq = 2000;
            boolean supportsIdle = true;
//            try {
//                if (folder instanceof IMAPFolder) {
//                    IMAPFolder f = (IMAPFolder)folder;
//                    f.idle();
//                    supportsIdle = true;
//                }
//            } catch (FolderClosedException fex) {
//                throw fex;
//            } catch (MessagingException mex) {
//                supportsIdle = false;
//            }
            for (;;) {
                if (supportsIdle && folder instanceof IMAPFolder) {
                    IMAPFolder f = (IMAPFolder)folder;
                    f.idle();
                    System.out.println("IDLE done");
                } else {
                    Thread.sleep(freq); // sleep for freq milliseconds

                    // This is to force the IMAP server to send us
                    // EXISTS notifications.
//                    folder.getMessageCount();
                }
            }
// =cz&S52f
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            try {
                if(folder!=null)
                folder.close(true);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }
    }
}