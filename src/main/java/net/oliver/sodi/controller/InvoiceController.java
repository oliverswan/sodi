package net.oliver.sodi.controller;

import net.oliver.sodi.model.*;
import net.oliver.sodi.service.IBackorderService;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.service.ISoldHistoryService;
import net.oliver.sodi.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
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

    @Autowired
    ISoldHistoryService soldHistoryService;

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
        Sort sort = new Sort(Sort.Direction.DESC, "_id");
        return invoiceService.findAll(buildPageRequest(pageNum,pageSize,""));
    }

    private PageRequest buildPageRequest(int pageNumber, int pageSize, String sortType) {
        Sort sort =  new Sort(Sort.Direction.DESC, "_id");
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
                    iitem.reCalculate(1);
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

    private String approveInvoice(Invoice invoice)
    {
        List<Invoice> xeroList = new ArrayList<Invoice>();
        List<Item> inventoryItems = new ArrayList<Item>();
        List<SoldHistory> soldHistories = new ArrayList<SoldHistory>();
        invoice.setStatus(1);
        // 是否缺货
        StringBuffer osb = new StringBuffer();
        boolean importToXero = false;
        Backorder bo = new Backorder();

        for(InvoiceItem iitem : invoice.getItems())
        {
            if(iitem.getInventoryItemCode().equals("SHIP"))
                continue;
            Item item= null;
            List<Item> itemResult  = itemService.findByCode(iitem.getInventoryItemCode());
            if(itemResult == null || itemResult.size()<1)
            {
                // 新增item
                item = new Item();
                item.setCode(iitem.getInventoryItemCode());
                item.setId(sequence.getNextSequence("item"));
                item.setStock(0);
                item.setAccountCode("4000");
                itemService.save(item);
            }else{
                item = itemResult.get(0);
            }
//                continue;

//            List<Backorder> bos = backorderService.findByInvoiceNumber(invoice.getInvoiceNumber());
//            if(bos.size()>0)
//            {
//                bo = bos.get(0);
//            }
            // 仓库信息

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
                // 更新还是创建backorder
                if(StringUtils.isBlank(bo.getInvoiceNumber()))
                {
                    List<Backorder> bos = backorderService.findByInvoiceNumber(invoice.getInvoiceNumber());
                    if(bos.size()>0)
                    {
                        bo = bos.get(0);
                    }else{
                        bo.setId(sequence.getNextSequence("backorder"));
                        bo.setCustomName(invoice.getContactName());
                        bo.setInvoiceNumber(invoice.getInvoiceNumber());
                    }
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
                item.reCalValue();
                // 更新订单item的数量
                iitem.setQuantity(iitem.getQuantity() - needmore);
                iitem.reCalculate(1);
            }else{
                // 更新售出后的库存
                item.setStock(stock);
                item.reCalValue();
            }
            if(iitem.getQuantity()>0 ) {
                importToXero = true;
                // 统计这个item 当月销售
                SoldHistory sh = soldHistoryService.getSoldHistory(invoice.getContactName(),iitem.getInventoryItemCode(), SystemStatus.getCurrentYM(), iitem.getQuantity());
                soldHistories.add(sh);
            }
            item.setSoldThisYear(sold);
            inventoryItems.add(item);
        }
        invoice.reCalculate();
        if(!importToXero)
        {
            StringBuffer oldnote = new StringBuffer(invoice.getOrderNote()==null?"":invoice.getOrderNote());
            oldnote.append("\r\n");
            oldnote.append("No stock,not shipped!");
            invoice.setOrderNote(oldnote.toString());

            if(!StringUtils.isBlank(bo.getInvoiceNumber()))
            {
                backorderService.save(bo);
                invoice.setOrderNote(osb.toString());
            }

            return "{'status':'not imported'}";
        }

        // 导入Xero
        if(importToXero) {
            try {
                XeroUtil.createInvoice(invoice);
            } catch (Exception e) {
                e.printStackTrace();
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                logger.info(str);
                return  "{'status':'fail','msg':"+e.getMessage()+" "+e.getClass().getCanonicalName()+"}";
            }
        }
        // 保存销售历史
        soldHistoryService.save(soldHistories);
        // 保存backorder
        if(!StringUtils.isBlank(bo.getInvoiceNumber()))
        {
            backorderService.save(bo);
            invoice.setOrderNote(osb.toString());
        }
        // 保存invoice
        invoiceService.update(invoice);
        // 更新本单所有的库存
        itemService.save(inventoryItems);
        // 重新清点库存额度
        ItemController.reSetFlag();
        return "{'status':'ok'}";
    }

    @RequestMapping(value = { "/approveSingle" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String approviceS(@RequestBody Invoice invoice)  {
        String x = this.approveInvoice(invoice);
        return x;
    }

    @RequestMapping(value = { "/undo" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String undo(@RequestBody Invoice invoice)  {

        // 0 删除backorder
        List<Backorder> bos = backorderService.findByInvoiceNumber(invoice.getInvoiceNumber());

        if(bos.size()>0)
        {
            for(Backorder bo : bos)
            {
                backorderService.delete(bo);
            }
        }
        // 1 恢复库存
        List<Item> result = new ArrayList<Item>();
        for(InvoiceItem iitem : invoice.getItems())
        {
            List<Item> all = itemService.findByCode(iitem.getInventoryItemCode());
            if(all.size()>0)
            {
                all.get(0).setStock(iitem.getQuantity()+all.get(0).getStock());
                result.add(all.get(0));
            }
        }
        if(result.size()>0)
            itemService.save(result);

        // 2 返回draft
        invoice.setStatus(0);
        invoiceService.save(invoice);
        return  "{'status':'ok'}";
    }


    @RequestMapping(value = { "/approveBackorder" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String approveBackorder(@RequestBody Invoice invoice)  {
        // 首先处理BackOrder
        try {
            backorderService.processInvoice( invoice);
        }catch (Exception e)
        {
            return "{'status':'fail'}";
        }
//        String x = this.approveInvoice(invoice);
        return  "{'status':'ok'}";
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


    @GetMapping("/querybycode")
    @ResponseBody
    public List<Invoice> querybycode(int id,String code)  {
        return invoiceService.findInvoiceByCode(id,code);
    }

    @RequestMapping(value = { "/xero" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String xero(@RequestBody Invoice invoice)  {
        Invoice inv = invoiceService.findById(invoice.getId());
        try {
            XeroUtil.createInvoice(inv);
        } catch (Exception e) {
            e.printStackTrace();
            return "{'status':'fail'}";
        }
        return "{'status':'ok'}";
    }

    @GetMapping("/check/{id}")
    @ResponseBody
    public String getItem(@PathVariable int id )  {

        Invoice invoice = invoiceService.findById(id);

        List<InvoiceItem> iItems = invoice.getItems();
        StringBuffer sb = new StringBuffer();
        for(InvoiceItem i : iItems)
        {
            String code = i.getInventoryItemCode();
            int quantity = i.getQuantity();
            List<Item> products = itemService.findByCode(code);

            if(products.size()>0)
            {
                int stock  = products.get(0).getStock();
                if(stock<quantity)
                {
                    int delta = quantity - stock;
                    sb.append(delta +" X "+ code).append("<br/>");
                }
            }
        }
        if(sb.toString().length()<=1)
        {
            return "We have all of them!";
        }else
        {
            return sb.toString();
        }
    }

    @GetMapping("/gobo/{id}")
    @ResponseBody
    public String gobo(@PathVariable int id )  {

        Invoice invoice = invoiceService.findById(id);

        List<InvoiceItem> iItems = invoice.getItems();
        StringBuffer sb = new StringBuffer();
        Backorder bo = new Backorder();
        bo.setId(sequence.getNextSequence("backorder"));
        bo.setCustomName(invoice.getContactName());
        bo.setInvoiceNumber(invoice.getInvoiceNumber());
        for(InvoiceItem i : iItems)
        {
            String code = i.getInventoryItemCode();
            if(!code.equals("SHIP"))
            {
                int quantity = i.getQuantity();
                bo.addItem(code,quantity);
            }
        }

        backorderService.save(bo);
        return "{'status':'ok'}";
    }
}
