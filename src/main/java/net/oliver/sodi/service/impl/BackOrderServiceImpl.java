package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IBackOrderDao;
import net.oliver.sodi.model.BackOrderReportEntry;
import net.oliver.sodi.model.Backorder;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoiceItem;
import net.oliver.sodi.service.IBackorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public List<BackOrderReportEntry> report() {
        Map<String,BackOrderReportEntry> temp = new HashMap<String,BackOrderReportEntry>();
        List<Backorder> orders = dao.findAll();
        for(Backorder order : orders)
        {
            for(Iterator iter = order.getOrders().entrySet().iterator();iter.hasNext();)
            {

                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter.next();
                String itemCode = entry.getKey();
                BackOrderReportEntry t;
                if(temp.containsKey(itemCode))
                {
                     t = temp.get(itemCode);
                }else{
                     t = new BackOrderReportEntry();
                    t.setItemCode(itemCode);
                    temp.put(itemCode,t);
                }
                t.addToTal(entry.getValue());
                t.addDistributeForCustomer(order.getCustomName(),entry.getValue());
            }
        }
        Collection<BackOrderReportEntry> valueCollection = temp.values();
        return new ArrayList<BackOrderReportEntry>(valueCollection);
    }

    @Override
    public List<Backorder> findByInvoiceNumber(String invoice_number) {
        return dao.findByInvoiceNumber(invoice_number);
    }

    @Override
    public Backorder findById(int id) {
        return dao.findOne(id);
    }

    @Override
    public void processInvoice(Invoice invoice) {
        List<Backorder> backorders = dao.findByCustomName(invoice.getContactName());


        for(InvoiceItem item : invoice.getItems())
        {
            String code = item.getInventoryItemCode();
            int quantity = item.getQuantity();

            // 需要优化 从backorder中找到 需求修改的code
            for(Backorder b : backorders)
            {
                int left = b.removeItem(code,quantity);
                if(left<0)
                {
                    quantity = -left;
                }else{
                    break;
                }
            }
        }

        Iterator<Backorder> it = backorders.iterator();
        while(it.hasNext()){
            Backorder x = it.next();
            if(x.getOrders().size()==0){
                it.remove();
                dao.delete(x);
            }
        }
        if(backorders.size()>0)
            dao.save(backorders);
    }

    @Override
    public void delete(Backorder bo) {
        dao.delete(bo);
    }
}
