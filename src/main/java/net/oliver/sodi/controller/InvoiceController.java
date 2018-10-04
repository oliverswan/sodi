package net.oliver.sodi.controller;

import net.oliver.sodi.model.*;
import net.oliver.sodi.service.IBackorderService;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.util.DateUtil;
import net.oliver.sodi.util.JsoupUtil;
import net.oliver.sodi.util.MongoAutoidUtil;
import net.oliver.sodi.util.XeroUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
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

    static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @RequestMapping(value = { "/add" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String addInvoice(@RequestBody Invoice invoice)  {
        invoice.setId(sequence.getNextSequence("invoice"));
        if(StringUtils.isBlank(invoice.getInvoiceDate()))
        {
            invoice.setInvoiceDate(JsoupUtil.dateFormat.format(new Date()));
            invoice.setDueDate(DateUtil.getMaxMonthDate(JsoupUtil.dateFormat.format(new Date())));
        }

        invoiceService.save(invoice);
        return "{'status':'ok'}";
    }

    @RequestMapping(value = { "/update" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String updateInvoice(@RequestBody Invoice invoice)  {
        invoiceService.update(invoice);
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
//    url = url + "pageNum=" + this.pageNum + '&pageSize=' + this.pageSize
    public InvoicesResult getAll(int pageNum, int pageSize)  {
        return invoiceService.findAll(buildPageRequest(pageNum,pageSize,""));
    }

    private PageRequest buildPageRequest(int pageNumber, int pageSize, String sortType) {
        Sort sort =  new Sort(Sort.Direction.ASC, "_id");;
//        if ("auto".equals(sortType)) {
//            sort = new Sort(Sort.Direction.ASC, "_id");
//        }
        //参数1表示当前第几页,参数2表示每页的大小,参数3表示排序
        return new PageRequest(pageNumber-1,pageSize,sort);
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
                    // 更新订单item的数量
                    iitem.setQuantity(iitem.getQuantity() - needmore);
                    iitem.reCalculate();
                }else{
                    // 更新仓库数量
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

    private void approveInvoice(Invoice invoice)
    {
        List<Invoice> xeroList = new ArrayList<Invoice>();
        List<Item> inventoryItems = new ArrayList<Item>();
        invoice.setStatus(1);
        // 是否缺货
        StringBuffer osb = new StringBuffer();
        boolean importToXero = false;
        for(InvoiceItem iitem : invoice.getItems())
        {
            if(iitem.getInventoryItemCode().equals("SHIP"))
                continue;
            List<Item> itemResult  = itemService.findByCode(iitem.getInventoryItemCode());
            if(itemResult == null || itemResult.size()<1)
                continue;
            Backorder bo = new Backorder();
            List<Backorder> bos = backorderService.findByInvoiceNumber(invoice.getInvoiceNumber());
            if(bos.size()>0)
            {
                bo = bos.get(0);
            }

            // 仓库信息
            Item item = itemResult.get(0);
            // 订单量
            int quantity = iitem.getQuantity();
            // 今年卖出的最新值
            int sold = item.getSoldThisYear()+quantity;
            // 本次销售后，剩余库存
            int stock = item.getStock() -quantity;
            // 除下库存,为满足本单需要新增的个数
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
                // Back Order Note
                osb.append(needmore+" X "+item.getCode()).append("\r\n");
                // 添加BackOrder的数量
                bo.addItem(item.getCode(),needmore);
                item.setStock(0);
                // 更新订单item的数量
                iitem.setQuantity(iitem.getQuantity() - needmore);
                if(iitem.getQuantity()>0)
                    importToXero = true;
                iitem.reCalculate();
            }else{
                // 更新售出后的库存
                item.setStock(stock);
            }
            item.setSoldThisYear(sold);
            inventoryItems.add(item);

            if(!StringUtils.isBlank(bo.getInvoiceNumber()))
            {
                backorderService.save(bo);
//                    osb.append(" on back order.");
                invoice.setOrderNote(osb.toString());
            }
        }
        invoice.reCalculate();
        if(!importToXero)
        {
            StringBuffer oldnote = new StringBuffer(invoice.getOrderNote()==null?"":invoice.getOrderNote());
            oldnote.append("\r\n");
            oldnote.append("No stock,not shipped!");
            invoice.setOrderNote(oldnote.toString());
        }

        // 保存invoice
        invoiceService.update(invoice);
        // 更新本单所有的库存
        itemService.save(inventoryItems);
        // 导入Xero
        if(importToXero)
            XeroUtil.createInvoice(invoice);

    }

    @RequestMapping(value = { "/approveSingle" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String approviceS(@RequestBody Invoice invoice)  {
        this.approveInvoice(invoice);
        return "{'status':'ok'}";
    }

    @RequestMapping(value = { "/approveMulti" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String approviceS(@RequestBody List<Invoice> invoices)  {
        for(Invoice invoice : invoices)
        {
            this.approveInvoice(invoice);
        }
        return "{'status':'ok'}";
    }

    @GetMapping("/query")
    @ResponseBody
    public List<Invoice> findLikeNameOrNumber(String customerName,String invoiceNumber,int pageNum,int pageSize)  {
        return invoiceService.findLikeNameOrNumber(customerName,invoiceNumber);
    }

    @RequestMapping(value = { "/xero" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String xero(@RequestBody Invoice invoice)  {
        Invoice inv = invoiceService.findById(invoice.getId());
        XeroUtil.createInvoice(inv);
        return "{'status':'ok'}";
    }
}
