package net.oliver.sodi.controller;


import net.oliver.sodi.model.User;
import net.oliver.sodi.service.IUserService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/users")
public class UserController {

    @Autowired
    IUserService userService;

    @Autowired
    MongoAutoidUtil sequence;

    @GetMapping("/{name}")
    @ResponseBody
    public User getItem(@PathVariable String name )  {
        return userService.findByName(name);
    }

    @RequestMapping(value = { "/add" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(@RequestBody User user)  {
        user.setId(sequence.getNextSequence("user"));
        userService.save(user);
        return "{'status':'ok'}";
    }

    @RequestMapping(value = { "/tempadd" }, method = { RequestMethod.GET }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save()  {
        User user = new User();
        user.setId(sequence.getNextSequence("user"));
        user.setName("oliver");
        user.setPassword("1234");
        userService.save(user);
        return "{'status':'ok'}";
    }
}
