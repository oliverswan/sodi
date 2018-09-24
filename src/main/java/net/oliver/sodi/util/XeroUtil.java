package net.oliver.sodi.util;

import com.connectifier.xeroclient.XeroClient;
import com.connectifier.xeroclient.models.Contact;
import com.connectifier.xeroclient.models.Invoice;
import com.connectifier.xeroclient.models.InvoiceType;
import com.connectifier.xeroclient.models.LineItem;
import com.xero.api.Config;
import com.xero.api.JsonConfig;
import net.oliver.sodi.model.InvoiceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class XeroUtil {



//    {
//        "AppType" : "PRIVATE",
//            "UserAgent": "SodiConnection-CDPB2R",
//            "ConsumerKey" : "FJSDAUTSFVRDZPNJKZUN6QJGXE4NDA",
//            "ConsumerSecret" : "MXZU5DUE9RBSN6UEYI38JBBPGWCBTL",
//            "PrivateKeyCert" :  "certs/public_privatekey.pfx",
//            "PrivateKeyPassword" :  "YourKeyPasssword"
//    }

    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    static XeroClient client;
    static final Logger logger = LoggerFactory.getLogger(XeroUtil.class);
    static
    {
        try {
            File f = new File(".");
            System.out.println(f.getAbsolutePath());
            Config config = JsonConfig.getInstance();
            Reader pemReader  = new FileReader(new File("./privatekey.pem"));
            client = new XeroClient(pemReader, config.getConsumerKey(), config.getConsumerSecret());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createInvoices(List<net.oliver.sodi.model.Invoice> sodiInvoices) {
        try {
            List<Invoice> ins = new ArrayList<Invoice>();
            for(net.oliver.sodi.model.Invoice sodiInvoice : sodiInvoices ) {
                Invoice in = new Invoice();
                in.getLineAmountTypes().add("Exclusive");
                in.setType(InvoiceType.ACCREC);
                in.setDate(dateFormat.parse(sodiInvoice.getInvoiceDate()));
                List<LineItem> items = new ArrayList<LineItem>();
                for (InvoiceItem sodiItem : sodiInvoice.getItems()) {
                    LineItem item = new LineItem();
                    item.setItemCode(sodiItem.getInventoryItemCode());
                    item.setUnitAmount(sodiItem.getUnitAmount());
                    item.setQuantity(new BigDecimal(sodiItem.getQuantity()));
                    item.setDescription(sodiItem.getDescription());
                    item.setTaxType("OUTPUT");//这里使用内部代码而非显示内容，澳大利亚的是GST 10%
//                    item.setTaxAmount(new BigDecimal(sodiItem.getUnitAmount()*sodiItem.getQuantity()*0.1));
                    item.setAccountCode(sodiItem.getAccountCode());
                    item.setLineAmount(sodiItem.getUnitAmount().multiply(new BigDecimal(sodiItem.getQuantity())));
                    items.add(item);
                }
                in.setLineItems(items);
                Contact contact = new Contact();
                contact.setName(sodiInvoice.getContactName());
                in.setContact(contact);
                in.setInvoiceNumber(sodiInvoice.getInvoiceNumber());
                in.setReference(sodiInvoice.getInvoiceNumber());
                ins.add(in);
            }
            List rr = client.createInvoices(ins);
        }catch (Exception e)
        {
            logger.error("Import to xero exception.."+e.getMessage());
            e.printStackTrace();
        }

    }


    public static void createInvoice(net.oliver.sodi.model.Invoice sodiInvoice) {
        try {
                Invoice in = new Invoice();
                in.getLineAmountTypes().add("Exclusive");
                in.setType(InvoiceType.ACCREC);
                in.setDate(dateFormat.parse(sodiInvoice.getInvoiceDate()));
                List<LineItem> items = new ArrayList<LineItem>();
                for (InvoiceItem sodiItem : sodiInvoice.getItems()) {
                    LineItem item = new LineItem();
                    item.setItemCode(sodiItem.getInventoryItemCode());
                    item.setUnitAmount(sodiItem.getUnitAmount());
                    item.setQuantity(new BigDecimal(sodiItem.getQuantity()));
                    item.setDescription(sodiItem.getDescription());
                    item.setTaxType("OUTPUT");//这里使用内部代码而非显示内容，澳大利亚的是GST 10%
//                    item.setTaxAmount(new BigDecimal(sodiItem.getUnitAmount()*sodiItem.getQuantity()*0.1));
                    item.setAccountCode(sodiItem.getAccountCode());
                    item.setLineAmount(sodiItem.getUnitAmount().multiply(new BigDecimal(sodiItem.getQuantity())));
                    items.add(item);
                }
                in.setLineItems(items);
                Contact contact = new Contact();
                contact.setName(sodiInvoice.getContactName());
                in.setContact(contact);
                in.setInvoiceNumber(sodiInvoice.getInvoiceNumber());
                in.setReference(sodiInvoice.getInvoiceNumber());
            List rr = client.createInvoice(in);
        }catch (Exception e)
        {
            logger.error("Import to xero exception.."+e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.info(str);
        }
    }
}
