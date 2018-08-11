package net.oliver.sodi.controller;

import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoiceItem;
import net.oliver.sodi.model.Item;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.util.MongoAutoidUtil;
import net.oliver.sodi.util.XeroUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/invoices")
public class InvoiceController {

    @Autowired
    IInvoiceService invoiceService;
    @Autowired
    IItemService itemService;

    @Autowired
    MongoAutoidUtil sequence;

    @RequestMapping(value = { "/add" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String addInvoice(@RequestBody Invoice invoice)  {
        invoice.setId(sequence.getNextSequence("invoice"));
        invoiceService.save(invoice);
        return "{'status':'ok'}";
    }

    @RequestMapping(value = { "/addmany" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String addInvoices(@RequestBody List<Invoice> list)  {
        invoiceService.saveInvoices(list);
        return "{'status':'ok'}";
    }

    @GetMapping("")
    @ResponseBody
    public List<Invoice> getAll()  {
        return invoiceService.findAll();
    }

    @GetMapping("/draft")
    @ResponseBody
    public List<Invoice> getDraft()  {
         return invoiceService.findDraft();
    }

    @GetMapping("/approveall")
    @ResponseBody
    public String approveallDraft()  {
        List<Invoice> drafts = invoiceService.findDraft();
        List<Item> inventoryItems = new ArrayList<Item>();

        for(Invoice invoice:drafts)
        {
            // 更新订单状态
            invoice.setStatus(1);
            // 更新库存
            for(InvoiceItem iitem : invoice.getItems())
            {
                if(iitem.getInventoryItemCode().equals("SHIP"))
                    continue;
                List<Item> itemResult  = itemService.findByCode(iitem.getInventoryItemCode());
                if(itemResult == null || itemResult.size()<1)
                    continue;
                Item item = itemResult.get(0);
                int quantity = iitem.getQuantity();
                int sold = item.getSoldThisYear()+quantity;
                int stock = item.getStock() -quantity;
                item.setSoldThisYear(sold);
                item.setStock(stock);
                inventoryItems.add(item);
            }
            itemService.save(inventoryItems);
        }

        invoiceService.saveInvoices(drafts);
        // 导入 Xero
        XeroUtil.createInvoices(drafts);
        return "sodi_OK";
    }

    @PostMapping(value = "/approveall2",consumes="text/plain")
    public String approveallDraft2(@RequestBody String body )  {
        List<Invoice> drafts = invoiceService.findDraft();
        List<Item> inventoryItems = new ArrayList<Item>();
        String[] mapping = body.split(",");
        Map<String,String> map = new HashMap<String,String>();
        for(String str:mapping)
        {
            String[] ary = str.split("@");
            map.put(ary[0],ary[1]);
        }
        for(Invoice invoice:drafts)
        {
            // invoice number
            invoice.setStatus(1);
            invoice.setInvoiceNumber(map.get(invoice.getReference()));

            // 更新库存
            for(InvoiceItem iitem : invoice.getItems())
            {
                if(iitem.getInventoryItemCode().equals("SHIP"))
                    continue;
                List<Item> itemResult  = itemService.findByCode(iitem.getInventoryItemCode());
                if(itemResult == null || itemResult.size()<1)
                    continue;
                Item item = itemResult.get(0);
                int quantity = iitem.getQuantity();
                int sold = item.getSoldThisYear()+quantity;
                int stock = item.getStock() -quantity;
                item.setSoldThisYear(sold);
                item.setStock(stock);
                inventoryItems.add(item);
            }
            itemService.save(inventoryItems);
        }

        invoiceService.saveInvoices(drafts);
        // 导入 Xero
        XeroUtil.createInvoices(drafts);
        return "{'status':'ok'}";
    }
}
