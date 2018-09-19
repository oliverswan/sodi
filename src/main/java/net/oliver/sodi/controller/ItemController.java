package net.oliver.sodi.controller;

import net.oliver.sodi.dao.IItemDao;
import net.oliver.sodi.model.Item;
import net.oliver.sodi.model.ItemResult;
import net.oliver.sodi.model.SalesResult;
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
    public ItemResult getAll(@RequestParam int echo)  {

        ItemResult r = new ItemResult();
        List<Item> l = service.findAll();
        r.setEcho(echo);
        r.setFiltered(l.size());
        r.setData(l);
        return r;
    }

    @GetMapping("/{code}")
    @ResponseBody
    public List<Item> getItem(@PathVariable String code )  {

        return service.findByCode(code);
    }


    @GetMapping("/query/{criteria}")
    @ResponseBody
    public List<Item> autoComplete(@PathVariable String criteria )  {

        return service.findItemAutoComplete(criteria);
    }

    @GetMapping("/salesmost/{number}")
    @ResponseBody
    public SalesResult autoComplete(@PathVariable int number )  {

        SalesResult result = new SalesResult();
        List<Item> r = service.findAllOrderBySoldThisYear();
        String[] l = new String[number];
        int[] d = new int[number];
        for(int i=0;i<number;i++)
        {
            l[i] = r.get(i).getCode();
            d[i] = r.get(i).getSoldThisYear();
        }
        result.setLabels(l);
        result.setDatasets(d);
        return result;
    }

    @RequestMapping(value = { "/save" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String addInvoices(@RequestBody Item item)  {
        service.save(item);
        return "{'status':'ok'}";
    }

    @RequestMapping(value = { "/add" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(@RequestBody Item item)  {
        item.setId(sequence.getNextSequence("contact"));
        service.save(item);
        return "{'status':'ok'}";
    }

}
