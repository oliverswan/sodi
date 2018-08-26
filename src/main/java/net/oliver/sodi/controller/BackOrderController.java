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

    @GetMapping("/complete/{id}")
    @ResponseBody
    public String getItem(@PathVariable int id )  {

        Backorder result = service.findById(id);
        if(result!=null)
        {
            result.setStatus(1);
            service.save(result);
        }
        return "ok";
    }

    @RequestMapping(value = { "/update" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String update(@RequestBody Backorder bo)  {
        service.save(bo);
        return "{'status':'ok'}";
    }
}
