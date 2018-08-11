package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IInvoiceDao;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.service.IInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceServiceImpl implements IInvoiceService {

    @Autowired
    IInvoiceDao dao;

    @Override
    public void save(Invoice invoice) {
        Invoice invoice_copy = dao.save(invoice);
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
}
