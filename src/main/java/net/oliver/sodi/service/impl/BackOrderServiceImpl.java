package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IBackOrderDao;
import net.oliver.sodi.model.*;
import net.oliver.sodi.service.IBackorderService;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BackOrderServiceImpl implements IBackorderService {
    @Autowired
    IBackOrderDao dao;

    @Autowired
    IItemService itemService;

    @Autowired
    MongoAutoidUtil sequence;

    @Autowired
    IInvoiceService invoiceService;

    @Override
    public void saveBackOrders(List<Backorder> list) {
        dao.save(list);
    }

    @Override
    public void save(Backorder bo) {
        if(bo.getCreatedTime() == null)
            bo.setCreatedTime(new Date());
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
    public void stockBackorders(Invoice invoice) {


        Backorder bo = new Backorder();
        bo.setId(sequence.getNextSequence("backorder"));
        bo.setInvoiceNumber(invoice.getInvoiceNumber());
        bo.setCustomName(invoice.getContactName());
        Map<String,Integer> orders = new HashMap<>();
        bo.setOrders(orders);
        for(InvoiceItem invoiceItem : invoice.getItems())
        {
            String code = invoiceItem.getInventoryItemCode();
            int quantity = invoiceItem.getQuantity();

            List<Item> is = itemService.findByCode(code);

            if(is.size()>0)
            {
                Item item = is.get(0);
                int stock = item.getStock();

                if(stock < quantity)
                {
                    // 修改订单
                    invoiceItem.setQuantity(stock);
                    // 修改backorder
                    int owed = quantity - stock;
                    orders.put(code,owed);
                    // 确保处理后没有BO
                }
            }
        }
        invoiceService.save(invoice);
        dao.save(bo);
    }

    @Override
    public void delete(Backorder bo) {
        dao.delete(bo);
    }
}
