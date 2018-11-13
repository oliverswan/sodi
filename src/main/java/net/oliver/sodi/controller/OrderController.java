package net.oliver.sodi.controller;


import net.oliver.sodi.model.InvoicesResult;
import net.oliver.sodi.model.Item;
import net.oliver.sodi.model.Order;
import net.oliver.sodi.model.OrderResult;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.service.IOrderService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/api/orders")
public class OrderController {

    @Autowired
    IOrderService service;

    @Autowired
    IItemService itemService;

    @Autowired
    MongoAutoidUtil sequence;

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
}
