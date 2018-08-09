package net.oliver.sodi.controller;

import net.oliver.sodi.dao.IInvoiceDao;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/api/invoice")
public class InvoiceController {

    @Autowired
    IInvoiceService invoiceService;

    @Autowired
    IInvoiceDao invoiceDao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MongoAutoidUtil sequence;

    @RequestMapping(value = { "/add" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String addInvoice(@RequestBody Invoice invoice)  {
        invoice.setId(sequence.getNextSequence("invoice"));
        invoiceService.save(invoice);
        return "{'status':'ok'}";
    }

    @RequestMapping(value = { "/addmany" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String addInvoices(@RequestBody List<Invoice> list)  {
        invoiceService.saveInvoices(list);
        return "{'status':'ok'}";
    }
}
