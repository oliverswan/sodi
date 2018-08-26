package net.oliver.sodi.util;

import net.oliver.sodi.model.InvoiceItem;
import net.oliver.sodi.model.Item;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.spring.SodiApplicationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemUtil {

    @Autowired
    static IItemService itemService;

    public static void fillInvoiceItem(String code, int quantity,InvoiceItem target)
    {
        if(itemService == null)
            itemService = SodiApplicationListener.applicationContext.getBean(IItemService.class);
        target.setInventoryItemCode(code);
        target.setQuantity(quantity);
        List<Item> l = itemService.findByCode(code);
        if(l.size()>0)
        {
            Item item = l.get(0);
//            item.setDescription(strArr[15]);
//            item.setUnitAmount(Double.parseDouble(strArr[17]));
//            item.setDiscount(strArr[18]);
//            item.setAccountCode(strArr[19]);
//            item.setTrackingName1(strArr[21]);
//            item.setTrackingOption1(strArr[22]);
//            item.setTrackingName2(strArr[23]);
//            item.setTrackingOption2(strArr[24]);
//            item.setCurrency(strArr[25]);
//            item.setBrandingTheme(strArr[26]);
//            item.setProduct_attribute(strArr[27]);
//            item.setProduct_subtotal_discount(strArr[28]);
//            item.setProduct_quantity(strArr[29]);
//            item.setTotalamount(MathUtil.trimDouble(item.getQuantity() * item.getUnitAmount() * 1.1));


            target.setDescription(item.getName());
            target.setUnitAmount(item.getPrice());
            target.setTaxType("GST on Income");

//            target.setTotalamount(target.getQuantity()*target.getUnitAmount());
            //TODO 設置ACCOUNTCODE
            target.setAccountCode("4000");
        }
    }
}
