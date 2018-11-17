package net.oliver.sodi.util;

import net.oliver.sodi.config.Const;
import net.oliver.sodi.model.Contact;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoiceItem;
import net.oliver.sodi.service.IContactService;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.spring.SodiApplicationListener;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class JsoupUtil {

    @Autowired
    static MongoAutoidUtil sequence;

    @Autowired
    static IContactService contactService;

    @Autowired
    static IInvoiceService invoiceSerivce;

    @Autowired
    static ItemUtil itemUtil;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    static final Logger logger = LoggerFactory.getLogger(JsoupUtil.class);

    private static void inital()
    {
        if (sequence == null)
            sequence = SodiApplicationListener.applicationContext.getBean(MongoAutoidUtil.class);
        if (contactService == null)
            contactService = SodiApplicationListener.applicationContext.getBean(IContactService.class);
        if (invoiceSerivce == null)
            invoiceSerivce = SodiApplicationListener.applicationContext.getBean(IInvoiceService.class);
        if (itemUtil == null)
            itemUtil = SodiApplicationListener.applicationContext.getBean(ItemUtil.class);
    }

    public static Invoice getInvoice(String html) {

        inital();

        try {
            html = html.replaceAll("(?i)<br[^>]*>", "br2n");
            Document doc = Jsoup.parse(html);
            Elements one = doc.select("td:contains(Order Number:)");//"span:contains(Ship to:)"
            String orderNumber =  one.get(1).siblingElements().get(0).text().trim();
            if(StringUtils.isBlank(orderNumber))
            {
                logger.info("Cant find orderNumer for email..");
                return null;
            }
            Invoice orderInvo = invoiceSerivce.findByOrderNumber(orderNumber);
            if(orderInvo !=null)
                return null;
            Invoice invoice = new Invoice();
            Elements pShip = doc.select("p:contains(Ship to:)");
            String[] shippingInfo = pShip.text().split("br2n");
            String customerName = shippingInfo[1].trim();

            String contactPerson = shippingInfo[2].trim();
            String address1 = shippingInfo[3].trim();
            String address2 = shippingInfo[4].trim();
            String address3 = shippingInfo[5].trim();
            String country = shippingInfo[6].trim();

            List<Contact> contacts = contactService.findByContactName(customerName.trim());
            Contact contact = new Contact();
            if (contacts.size() > 0) {
                contact = contacts.get(0);
            }else {
                // 提取客户信息
                contact.setId(sequence.getNextSequence("contact"));
                contact.setContactName(customerName);
                contact.setPoCountry(country);
                contact.setPoAddressLine1(address1);
                contact.setPoAddressLine2(address2);
                contact.setPoAddressLine3(address3);
                contactService.save(contact);
            }

            Elements itemTr = doc.select("tr:contains(Product Name)");
            double discount = 1;
            double gst = 1;
//            if(this.contactName === "Baykarts limited"||this.contactName === "Formula Challenge Limited"||this.contactName === "Pro Karts"||this.contactName === "Daytona Raceway")
            if(contact.getContactName().equals("Baykarts limited")||contact.getContactName().equals("Formula Challenge Limited")
                    ||contact.getContactName().equals("Pro Karts")||contact.getContactName().equals("Daytona Raceway"))
            {
                gst =0;
            }
            if(contact.getContactName().equals("The Kart Centre")||contact.getContactName().equals("Ultimate Karting Sydney"))
            {
                discount = MathUtil.trimDouble(0.80);
            }
            Elements itemTrs =itemTr.get(2).siblingElements();
            for (int i = 0; i < itemTrs.size() - 2; i++) {
                Elements tds = itemTrs.get(i).select("td");
                InvoiceItem iItem = new InvoiceItem();
                itemUtil.fillInvoiceItem(tds.get(0).text().trim(), Integer.parseInt(tds.get(1).text().trim()), iItem,1,1);
                invoice.addItem(iItem);
            }
            // 自动添加shiping fee
            InvoiceItem shipItem = new InvoiceItem();
            itemUtil.fillInvoiceItem("SHIP", 1, shipItem,1,1);
            shipItem.setUnitAmount(0);
            invoice.addItem(shipItem);

            invoice.setMoblie(contact.getMobile());
            invoice.setTel(contact.getPhone());
            invoice.setPocity(contact.getPoCity());
            invoice.setContactName(contact.getContactName());
            invoice.setContactPerson(contact.getPersonName());
            invoice.setPocountry(contact.getPoCountry());
            invoice.setPopostalcode(contact.getPoPostalCode());
            invoice.setPoaddressline1(contact.getPoAddressLine1());
            invoice.setPoaddressline2(contact.getPoAddressLine2());
            invoice.setPoaddressline3(contact.getPoAddressLine3());
            invoice.setPoaddressline4(contact.getPoAddressLine4());
            invoice.setEmailAddress(contact.getEmailAddress());
            invoice.setPoregion(contact.getPoRegion());

            invoice.setId(sequence.getNextSequence("invoice"));
            //orderNumber
            invoice.setOrderNumber(orderNumber);
            invoice.setStatus(0);

            //03/08/2018 23:55:50
            invoice.setInvoiceNumber(Const.InvoiceNumerPrefix+sequence.getNextSequence("invoiceNumber"));
            invoice.setReference(""+sequence.getNextSequence("invoiceReference"));

            //
            invoice.setInvoiceDate(dateFormat.format(new Date()));
            //TODO 修改duedate
            invoice.setDueDate(DateUtil.getMaxMonthDate(dateFormat.format(new Date())));
            invoice.reCalculate();
            return invoice;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception Occurs during analyst mail.."+e.getMessage()+" "+e.getClass().getCanonicalName());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.info(str);
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = new FileInputStream("D:/sodiInvoiceNoClass.txt");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
            reader.close();
            is.close();

            JsoupUtil.getInvoice(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
