package net.oliver.sodi.util;

import net.oliver.sodi.model.Contact;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoiceItem;
import net.oliver.sodi.service.IContactService;
import net.oliver.sodi.spring.SodiApplicationListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsoupUtil {

    @Autowired
    static MongoAutoidUtil sequence;

    @Autowired
    static IContactService contactService;

    @Autowired
    static ItemUtil itemUtil;


    //    static {
//        sequence = new MongoAutoidUtil();
//        contactService = new ContactServiceImpl();
//    }
    public static Invoice getInvoice(String html)  {

        if(sequence ==null)
            sequence =SodiApplicationListener.applicationContext.getBean(MongoAutoidUtil.class);
        if(contactService ==null)
            contactService =SodiApplicationListener.applicationContext.getBean(IContactService.class);
        if(itemUtil ==null)
            itemUtil =SodiApplicationListener.applicationContext.getBean(ItemUtil.class);

        try {
        html = html.replaceAll("(?i)<br[^>]*>", "br2n");
        Document doc = Jsoup.parse(html);
        Elements customerInfo = doc.select("p.MsoNormal");
        Invoice invoice = new Invoice();

        String orderNumber = customerInfo.get(5).text();

        String date = customerInfo.get(7).text();
        Elements customerInfo2 = doc.select("span:contains(Bill to:)");
        String customerName = customerInfo2.get(0).parent().parent().text().split("br2n")[1].trim();
        Elements tables = doc.select("table.MsoNormalTable");//<table class="MsoNormalTable"
        Elements TRs = tables.get(4).select("tr");

        invoice.setContactName(customerName);

        List<Contact> contacts = contactService.findByContactName(customerName.trim());
        if(contacts.size()>0)
        {
            Contact contact = contacts.get(0);
            invoice.setPOCity(contact.getPoCity());
            invoice.setPOCountry(contact.getPoCountry());
            invoice.setPOPostalCode(contact.getPoPostalCode());
            invoice.setPOAddressLine1(contact.getPoAddressLine1());
            invoice.setPOAddressLine2(contact.getPoAddressLine2());
            invoice.setPOAddressLine3(contact.getPoAddressLine3());
            invoice.setPOAddressLine4(contact.getPoAddressLine4());
            invoice.setEmailAddress(contact.getEmailAddress());
            invoice.setPORegion(contact.getPoRegion());
        }
        invoice.setId(sequence.getNextSequence("invoice"));
        invoice.setOrderNumber(orderNumber);
        invoice.setStatus(0);

        for(int i=1;i<TRs.size()-2;i++)
        {
            Elements tds = TRs.get(i).select("td");
            InvoiceItem iItem = new InvoiceItem();
            itemUtil.fillInvoiceItem(tds.get(0).text().trim(),Integer.parseInt(tds.get(1).text().trim()),iItem);
            invoice.addItem(iItem);
        }
        return invoice;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return  null;
    }


    public static void main(String[] args) {
        try {
            JsoupUtil.getInvoice("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
