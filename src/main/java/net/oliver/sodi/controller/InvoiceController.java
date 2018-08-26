package net.oliver.sodi.controller;

import net.oliver.sodi.model.Backorder;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoiceItem;
import net.oliver.sodi.model.Item;
import net.oliver.sodi.service.IBackorderService;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.util.MongoAutoidUtil;
import net.oliver.sodi.util.XeroUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/api/invoices")
public class InvoiceController {

    @Autowired
    IInvoiceService invoiceService;
    @Autowired
    IItemService itemService;

    @Autowired
    MongoAutoidUtil sequence;

    @Autowired
    IBackorderService backorderService;

    @RequestMapping(value = { "/add" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String addInvoice(@RequestBody Invoice invoice)  {
        invoice.setId(sequence.getNextSequence("invoice"));
        invoiceService.save(invoice);
        return "{'status':'ok'}";
    }

    @RequestMapping(value = { "/update" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String updateInvoice(@RequestBody Invoice invoice)  {
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
    @ResponseBody
    public String approveallDraft2(@RequestBody(required = false) String body )  {
        List<Invoice> drafts = invoiceService.findDraft();
        List<Item> inventoryItems = new ArrayList<Item>();
        /*Map<String,String> map = new HashMap<String,String>();
        if(!StringUtils.isBlank(body))
        {
            String[] mapping = body.split(",");
            for(String str:mapping)
            {
                String[] ary = str.split("@");
                map.put(ary[0],ary[1]);
            }
        }*/

        for(Invoice invoice:drafts)
        {
            // invoice number
            invoice.setStatus(1);
            /*if(map.containsKey(invoice.getReference()))
                invoice.setInvoiceNumber(map.get(invoice.getReference()));
            else*/

            // 更新库存
            // 查找backOrder
            StringBuffer osb = new StringBuffer();
            for(InvoiceItem iitem : invoice.getItems())
            {
                if(iitem.getInventoryItemCode().equals("SHIP"))
                    continue;
                List<Item> itemResult  = itemService.findByCode(iitem.getInventoryItemCode());
                if(itemResult == null || itemResult.size()<1)
                    continue;
                Backorder bo = new Backorder();
                Item item = itemResult.get(0);
                int quantity = iitem.getQuantity();

                int sold = item.getSoldThisYear()+quantity;// 今年卖出的最新值
                int stock = item.getStock() -quantity;// 本次销售后，剩余库存
                int needmore = 0;

                if(stock < 0 )
                {
                    if(StringUtils.isBlank(bo.getInvoiceNumber())){
                        bo.setId(sequence.getNextSequence("backorder"));
                        bo.setCustomName(invoice.getContactName());
                        bo.setInvoiceNumber(invoice.getInvoiceNumber());
                    }
                    if(item.getStock()>0)
                    {
                         needmore = quantity - item.getStock();
                    }else{
                         needmore = quantity;
                    }
                    osb.append(needmore+" x "+item.getCode()).append(",");

                    bo.addItem(item.getCode(),needmore);
                    item.setStock(0);
                    iitem.setQuantity(iitem.getQuantity() - needmore);
                }else{

                    item.setStock(stock); // 更新售出后的库存
                }
                item.setSoldThisYear(sold);
                inventoryItems.add(item);

                if(!StringUtils.isBlank(bo.getInvoiceNumber()))
                {
                    backorderService.save(bo);
//                    osb.append(" on back order.");
                    invoice.setOrderNote(osb.toString());
                }

                invoice.reCalculate();
            }

            itemService.save(inventoryItems);
        }

        invoiceService.saveInvoices(drafts);
        // 导入 Xero
        XeroUtil.createInvoices(drafts);
        return "{'status':'ok'}";
    }
}
