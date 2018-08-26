package net.oliver.sodi.mail;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

import java.net.URI;

public class ExchangeConnection {

    private final ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2); // change to whatever server you are running, though 2010_SP2 is the most recent version the Api supports

    public ExchangeConnection(String username, String password) {
        try {
            service.setCredentials(new WebCredentials(username, password));
            service.setUrl(new URI("https://sodirentalkarts.com.au/ews/exchange.asmx"));
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public boolean sendEmail(String subject, String message,String recipient) {
        try {
            EmailMessage email = new EmailMessage(service);
            email.setSubject(subject);
            email.setBody(new MessageBody(message));
//            for (String fileName : fileNames) email.getAttachments().addFileAttachment(fileName);
            email.getToRecipients().add(recipient);
            email.sendAndSaveCopy();
            return true;
        }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static void main(String[] args) {
        ExchangeConnection c = new ExchangeConnection("info@sodirentalkarts.com.au","Maiyang9");
        c.sendEmail("subject","xx","oliver_reg@126.com");
    }
}
