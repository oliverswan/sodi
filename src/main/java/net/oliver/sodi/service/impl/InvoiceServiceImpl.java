package net.oliver.sodi.service.impl;

import net.oliver.sodi.config.Const;
import net.oliver.sodi.dao.IInvoiceDao;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoicesResult;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    public InvoicesResult findAll(PageRequest request) {
        InvoicesResult result = new InvoicesResult();
        List<Invoice> r3 = new ArrayList<Invoice>();
        //TODO 分析如何将Page转为List
        Page<Invoice> result2 =  dao.findAll(request);
        result.setMessage(String.valueOf(result2.getTotalElements()));
        result2.forEach(new Consumer<Invoice>() {
            @Override
            public void accept(Invoice invoice) {
                r3.add(invoice);
            }
        });
        result.setData(r3);
        return result;
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
