package net.oliver.sodi.util;

import com.connectifier.xeroclient.XeroClient;
import com.connectifier.xeroclient.models.Contact;
import com.connectifier.xeroclient.models.Invoice;
import com.connectifier.xeroclient.models.InvoiceType;
import com.connectifier.xeroclient.models.LineItem;
import com.xero.api.Config;
import com.xero.api.JsonConfig;
import net.oliver.sodi.model.InvoiceItem;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
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
                in.setType(InvoiceType.ACCREC);
                in.setDate(dateFormat.parse(sodiInvoice.getInvoiceDate()));
                List<LineItem> items = new ArrayList<LineItem>();
                for (InvoiceItem sodiItem : sodiInvoice.getItems()) {
                    LineItem item = new LineItem();
                    item.setItemCode(sodiItem.getInventoryItemCode());
                    item.setUnitAmount(new BigDecimal(sodiItem.getUnitAmount()));
                    item.setQuantity(new BigDecimal(sodiItem.getQuantity()));
                    item.setDescription(sodiItem.getDescription());
                    item.setTaxType("OUTPUT");//这里使用内部代码而非显示内容，澳大利亚的是GST 10%
//                    item.setTaxAmount(new BigDecimal(sodiItem.getUnitAmount()*sodiItem.getQuantity()*0.1));
                    item.setAccountCode(sodiItem.getAccountCode());
                    item.setLineAmount(new BigDecimal(sodiItem.getUnitAmount()*sodiItem.getQuantity()));
                    items.add(item);
                }
                in.setLineItems(items);
                Contact contact = new Contact();
                contact.setName("Martin Hudson");
                in.setContact(contact);
                in.setInvoiceNumber(sodiInvoice.getInvoiceNumber());
                in.setReference(sodiInvoice.getInvoiceNumber());
                ins.add(in);
            }
            List rr = client.createInvoices(ins);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
