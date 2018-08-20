package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IBackOrderDao;
import net.oliver.sodi.model.Backorder;
import net.oliver.sodi.service.IBackorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class BackOrderServiceImpl implements IBackorderService {
    @Autowired
    IBackOrderDao dao;

    @Override
    public void saveBackOrders(List<Backorder> list) {
        dao.save(list);
    }

    @Override
    public void save(Backorder bo) {
        dao.save(bo);
    }


    @Override
    public List<Backorder> findNotCompleted() {
        return dao.findByStatusLessThan(1);
    }

    @Override
    public Map<String, Integer> report() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        List<Backorder> orders = dao.findAll();
        for(Backorder order : orders)
        {
            for(Iterator iter = order.getOrders().entrySet().iterator();iter.hasNext();)
            {
                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter.next();
                if(result.containsKey(entry.getKey()))
                {
                    result.put(entry.getKey(),result.get(entry.getKey())+entry.getValue());
                }else
                {
                    result.put(entry.getKey(),entry.getValue());
                }
            }
        }
        return result;
    }

    @Override
    public List<Backorder> findByInvoiceNumber(String invoice_number) {
        return dao.findByInvoiceNumber(invoice_number);
    }
}
