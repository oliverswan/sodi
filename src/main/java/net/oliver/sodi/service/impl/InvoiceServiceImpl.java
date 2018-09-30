package net.oliver.sodi.service.impl;

import net.oliver.sodi.config.Const;
import net.oliver.sodi.dao.IInvoiceDao;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoicesResult;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceServiceImpl implements IInvoiceService {

    @Autowired
    IInvoiceDao dao;

    @Autowired
    MongoAutoidUtil sequence;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void save(Invoice invoice) {
        invoice.setReference(sequence.getNextSequence("invoiceReference")+"");
        invoice.setInvoiceNumber(Const.InvoiceNumerPrefix+sequence.getNextSequence("invoiceNumber"));
        dao.save(invoice);
    }

    @Override
    public void update(Invoice invoice) {
        invoice.reCalculate();
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
//        List<Invoice> r3 = new ArrayList<Invoice>();
        //TODO 分析如何将Page转为List
        List<Invoice> result2 =  dao.findByStatus(1,request);
//        result.setMessage(String.valueOf(result2.getTotalElements()));
//        result2.forEach(new Consumer<Invoice>() {
//            @Override
//            public void accept(Invoice invoice) {
//                r3.add(invoice);
//            }
//        });
        result.setData(result2);
        return result;
    }

    @Override
    public InvoicesResult findByStatus(PageRequest request) {
        return null;
    }

    @Override
    public List<Invoice> findDraft() {
        return dao.findByStatus(0);
    }

    @Override
    public Invoice findById(int id) {
        return dao.findOne(id);
    }

    @Override
    public Invoice findByOrderNumber(String number) {
        List<Invoice> list = dao.findByOrderNumber(number);
        if(list.size()>0)
            return  list.get(0);
        return null;
    }

    @Override
    public List<Invoice> findLikeNameOrNumber(String customerName, String InvoiceNumber) {

//        Pattern pattern1 = Pattern.compile("^.*"+customerName+".*$", Pattern.CASE_INSENSITIVE);

        Criteria c1 = Criteria.where("contactName").is(customerName);
        Criteria c2 = Criteria.where("invoiceNumber").is(InvoiceNumber);
        Query query = new Query(new Criteria().orOperator(c1,c2));
        return mongoTemplate.find(query,Invoice.class);
    }
}
