package net.oliver.sodi.controller;

import net.oliver.sodi.model.BackOrderResult;
import net.oliver.sodi.model.Backorder;
import net.oliver.sodi.service.IBackorderService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/backorders")
public class BackOrderController {

    @Autowired
    IBackorderService service;

    @Autowired
    MongoAutoidUtil sequence;

    @Autowired
    MongoTemplate mongoTemplate;

    @GetMapping("")
    @ResponseBody
    public BackOrderResult findAll(@RequestParam int echo)  {

        BackOrderResult r = new BackOrderResult();
        List<Backorder> l = service.findAll();
        r.setEcho(echo);
        r.setFiltered(l.size());
        r.setData(l);
        return r;
//        return service.findNotCompleted();
    }

    @GetMapping("/complete/{id}")
    @ResponseBody
    public String getItem(@PathVariable int id )  {

        Backorder result = service.findById(id);
        service.delete(result);
//        if(result!=null)
//        {
//            result.setStatus(1);
//            service.save(result);
//        }
        return "ok";
    }

    @RequestMapping(value = { "/update" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String update(@RequestBody Backorder bo)  {
        service.save(bo);
        return "{'status':'ok'}";
    }


    @GetMapping("/addItem")
    @ResponseBody
    public String save(String invoiceNumber,String customerName,String code,int quantity)  {
//        bo.setId(sequence.getNextSequence("backorder"));
//        service.save(bo);

        List<Backorder> list = service.findByInvoiceNumber(invoiceNumber);
        if(list.size()>0)
        {
            Backorder exitBo = list.get(0);
            exitBo.addItem(code,quantity);
            service.save(exitBo);
        }else{
            Backorder newBO = new Backorder();
            newBO.setId(sequence.getNextSequence("backorder"));
            newBO.setInvoiceNumber(invoiceNumber);
            newBO.setCustomName(customerName);
            Map orders = new HashMap();
            orders.put(code,quantity);
            newBO.setOrders(orders);
            service.save(newBO);
        }
        return "{'status':'ok'}";
    }

    @GetMapping("/removeItem")
    @ResponseBody
    public String remove(String invoiceNumber,String customerName,String code,int quantity)  {
//        bo.setId(sequence.getNextSequence("backorder"));
//        service.save(bo);

        List<Backorder> list = service.findByInvoiceNumber(invoiceNumber);
        if(list.size()>0)
        {
            Backorder exitBo = list.get(0);
            exitBo.removeItem(code,quantity);
            service.save(exitBo);
        }
        return "{'status':'ok'}";
    }

    @GetMapping("/markOrdered")
    @ResponseBody
    public String markOrdered()  {
//        bo.setId(sequence.getNextSequence("backorder"));
//        service.save(bo);
        try
        {
            List<Backorder> list = service.findNotCompleted();
            if(list.size()>0)
            {
                for(Backorder bo : list)
                {
                    bo.setStatus(1);
                }

                service.saveBackOrders(list);
            }
            return "{'status':'ok'}";
        }catch (Exception x)
        {

            return "{'status':'"+x.getClass().getCanonicalName()+"'}";
        }


    }

}
