package net.oliver.sodi.controller;

import net.oliver.sodi.model.Contact;
import net.oliver.sodi.service.IContactService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/contacts")
public class ContactController {

    @Autowired
    MongoAutoidUtil sequence;

    @Autowired
    IContactService service;

    @RequestMapping(value = { "/add" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(@RequestBody Contact contact)  {
        contact.setId(sequence.getNextSequence("contact"));
        service.save(contact);
        return "{'status':'ok'}";
    }


    @RequestMapping(value = { "/tempadd" }, method = { RequestMethod.GET }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save()  {


        Contact contact = new Contact();
        contact.setId(sequence.getNextSequence("contact"));
        contact.setContactName("ace karts");
        contact.setPersonName("russ occhipinti");
        contact.setPoAddressLine1("20 carrington drive sunshine");
        contact.setPoAddressLine2("melbourne, Victoria 3020");
        contact.setPoCity("melbourne");
        contact.setPoCountry("Australia");
        contact.setMobile("0416133448");
        contact.setPhone("93605005");
        contact.setPoPostalCode("3020");
        contact.setPoRegion("Victoria");
        service.save(contact);
        return "{'status':'ok'}";
    }
}
