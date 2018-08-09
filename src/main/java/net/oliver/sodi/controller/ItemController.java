package net.oliver.sodi.controller;

import net.oliver.sodi.dao.IInvoiceDao;
import net.oliver.sodi.dao.IItemDao;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.Item;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/api/items")
public class ItemController {

    @Autowired
    IItemService service;

    @Autowired
    IItemDao dao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MongoAutoidUtil sequence;

    @GetMapping("")
    @ResponseBody
    public List<Item> getAll()  {
        return service.findAll();
    }

}
