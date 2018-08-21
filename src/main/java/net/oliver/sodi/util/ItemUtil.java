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
            target.setDescription(item.getName());
            target.setUnitAmount(item.getPrice());
            target.setTotalamount(target.getQuantity()*target.getUnitAmount());
            target.setAccountCode("4000");
        }
    }
}
