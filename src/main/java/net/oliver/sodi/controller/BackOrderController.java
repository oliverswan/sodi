package net.oliver.sodi.controller;

import net.oliver.sodi.model.BackOrderResult;
import net.oliver.sodi.model.Backorder;
import net.oliver.sodi.service.IBackorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/api/backorders")
public class BackOrderController {

    @Autowired
    IBackorderService service;

    @GetMapping("")
    @ResponseBody
    public BackOrderResult findNotCompleted(@RequestParam int echo)  {

        BackOrderResult r = new BackOrderResult();
        List<Backorder> l = service.findNotCompleted();
        r.setEcho(echo);
        r.setFiltered(l.size());
        r.setData(l);
        return r;
//        return service.findNotCompleted();
    }

    @GetMapping("/complete/{invoice_number}")
    @ResponseBody
    public String getItem(@PathVariable String invoice_number )  {

        List<Backorder> result = service.findByInvoiceNumber(invoice_number);
        if(result.size()>0)
        {
            Backorder order =result.get(0);
            order.setStatus(1);
            service.save(order);
        }
        return "ok";
    }

}
