package net.oliver.sodi.controller;


import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import net.oliver.sodi.mail.SendMail;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.util.InvoiceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;

@Controller
@RequestMapping(value = "/api/mail")
public class MailController {

    @Autowired
    IInvoiceService invoiceService;

    @Autowired
    InvoiceGenerator invo;

    @Autowired
    SendMail sendMail;

    @GetMapping("/{id}")
    @ResponseBody
    public void getItem(@PathVariable int id )  {
        Invoice invoice = invoiceService.findById(id);
        String email = invoice.getEmailAddress();

       Document document = new Document();
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, os);
            invo.createInvoice(document,writer,invoice);
            sendMail.doSendHtmlEmail("invoice","",email,os);
            invoice.setStatus(3);
            invoiceService.save(invoice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
