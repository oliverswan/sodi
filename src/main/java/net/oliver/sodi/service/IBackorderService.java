package net.oliver.sodi.service;

import net.oliver.sodi.model.Backorder;

import java.util.List;
import java.util.Map;

public interface IBackorderService {

    public void saveBackOrders(List<Backorder> list);
    public void save(Backorder bo);
//    public void update(Backorder bo);
    public List<Backorder> findNotCompleted();
    public Map<String,Integer> report();
    public List<Backorder> findByInvoiceNumber(String invoice_number);

}
