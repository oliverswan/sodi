package net.oliver.sodi.controller;

import net.oliver.sodi.model.Item;
import net.oliver.sodi.model.ItemResult;
import net.oliver.sodi.model.Todo;
import net.oliver.sodi.service.ITodoService;
import net.oliver.sodi.service.IUserService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/api/todo")
public class TodoController {


    @Autowired
    ITodoService service;

    @Autowired
    MongoAutoidUtil sequence;

//    @GetMapping("/{name}")
//    @ResponseBody
//    public User getItem(@PathVariable String name )  {
//        return userService.findByName(name);
//    }

    @GetMapping("")
    @ResponseBody
    public List<Todo> getAll()  {
        return service.findAll();
    }

    @RequestMapping(value = { "/save" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(@RequestBody Todo todo)  {

        if( todo.getId()== 0 )
            todo.setId(sequence.getNextSequence("todo"));
        service.save(todo);
        return "{'status':'ok'}";
    }




}
