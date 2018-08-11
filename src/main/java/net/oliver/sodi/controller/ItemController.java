package net.oliver.sodi.controller;

import net.oliver.sodi.dao.IItemDao;
import net.oliver.sodi.model.Item;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/{code}")
    @ResponseBody
    public List<Item> getItem(@PathVariable String code )  {

        return service.findByCode(code);
    }

}
