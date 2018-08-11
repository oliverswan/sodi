package net.oliver.sodi.controller;

import net.oliver.sodi.model.Backorder;
import net.oliver.sodi.service.IBackorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/api/backorders")
public class BackOrderController {

    @Autowired
    IBackorderService service;

    @GetMapping("")
    @ResponseBody
    public List<Backorder> getAll()  {
        return service.findAll();
    }

}
