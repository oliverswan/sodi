package net.oliver.sodi.controller;


import net.oliver.sodi.http.ItakaShop;
import net.oliver.sodi.model.*;
import net.oliver.sodi.service.IBackorderService;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.service.IOrderService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping(value = "/api/orders")
public class OrderController {

    @Autowired
    IOrderService service;

    @Autowired
    IItemService itemService;

    @Autowired
    MongoAutoidUtil sequence;

    @Autowired
    IBackorderService backorderService;


    static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private  static ConcurrentHashMap<String,String> codeToId = new ConcurrentHashMap<String,String>();

    private PageRequest buildPageRequest(int pageNumber, int pageSize, String sortType) {
        Sort sort =  new Sort(Sort.Direction.ASC, "_id");;
//        if ("auto".equals(sortType)) {
//            sort = new Sort(Sort.Direction.ASC, "_id");
//        }
        //参数1表示当前第几页,参数2表示每页的大小,参数3表示排序
        return new PageRequest(pageNumber-1,pageSize,sort);
    }

    @GetMapping("")
    @ResponseBody
//    url = url + "pageNum=" + this.pageNum + '&pageSize=' + this.pageSize
    public OrderResult getAll(int pageNum, int pageSize)  {
        return service.findAll(this.buildPageRequest(pageNum,pageSize,""));
    }


    @GetMapping("/create")
    @ResponseBody
//    url = url + "pageNum=" + this.pageNum + '&pageSize=' + this.pageSize
    public String create(int month)  {
        if(month < 1)
        {
            return "should bigger than 1";
        }
        List<Item> items =  itemService.findAllForReorder(month);
        Order order = new Order();
        order.setId(sequence.getNextSequence("order"));
        order.setName(String.valueOf(System.currentTimeMillis()));
        for(Item item : items)
        {
            String code = item.getCode();
            int reorder =  (int)Math.rint(month*item.getSpm()-item.getStock());
            order.addItem(code,reorder);
        }
        service.save(order);
        return "ok";
    }


    @GetMapping("/createbackorder")
    @ResponseBody
    public String createbackorder()  {
        Order order = new Order();
          // 0.遍历backorders
        List<BackOrderReportEntry> entries = backorderService.report(0);
        Collections.sort(entries);

        for(BackOrderReportEntry boentry : entries)
        {
            order.addItem(boentry.getItemCode(),boentry.getTotal());
        }
        service.save(order);
        return "ok";
    }

    @GetMapping("/addcart")
    @ResponseBody
//    url = url + "pageNum=" + this.pageNum + '&pageSize=' + this.pageSize
    public String addcart(int id)  {
        // 0.首先找到order
        Order order = service.findById(id);
        if(order != null)
        {


            // 1.遍历所有item 添加到order
            for(Iterator iter = order.getItems().entrySet().iterator();iter.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) iter.next();

                // 2.准备id
                String itakaId = codeToId.get(entry.getKey());
                if(itakaId == null)
                {
                    List<Item> list = itemService.findByCode((String) entry.getKey());
                    if(list.size()>0)
                    {
                        if(list.get(0).getItakaId() == null)
                        {
                            logger.info("Cant add "+list.get(0).getCode()+" to itaka ship, reason : no product id found.");
                            continue;
                        }
                        itakaId = list.get(0).getItakaId();
                        codeToId.putIfAbsent(list.get(0).getCode(),itakaId);
                    }else{
                        logger.info("Cant add "+list.get(0).getCode()+" to itaka ship, reason : cant find such item code.");
                        continue;
                    }
                }
                ItakaShop.addCart(itakaId,String.valueOf(entry.getValue()));
            }
            ItakaShop.reset();
            return "ok";
        }
        return "order is null";
    }
}
