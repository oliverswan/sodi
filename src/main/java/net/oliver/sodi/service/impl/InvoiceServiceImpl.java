package net.oliver.sodi.service.impl;

import net.oliver.sodi.config.Const;
import net.oliver.sodi.dao.IInvoiceDao;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceServiceImpl implements IInvoiceService {

    @Autowired
    IInvoiceDao dao;

    @Autowired
    MongoAutoidUtil sequence;

    @Override
    public void save(Invoice invoice) {
        invoice.setReference(sequence.getNextSequence("invoiceReference")+"");
        invoice.setInvoiceNumber(Const.InvoiceNumerPrefix+sequence.getNextSequence("invoiceNumber"));
        dao.save(invoice);
    }

    @Override
    public void saveInvoices(List<Invoice> list) {
        dao.save(list);
    }

    @Override
    public List<Invoice> findAll() {
        return dao.findAll();
    }

    @Override
    public List<Invoice> findDraft() {
        return dao.findByStatus(0);
    }

    @Override
    public Invoice findById(int id) {
        return dao.findOne(id);
    }
}
