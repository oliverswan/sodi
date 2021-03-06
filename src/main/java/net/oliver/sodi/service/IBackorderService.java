package net.oliver.sodi.service;

import net.oliver.sodi.model.BackOrderReportEntry;
import net.oliver.sodi.model.Backorder;
import net.oliver.sodi.model.Invoice;

import java.util.List;

public interface IBackorderService {

    public void saveBackOrders(List<Backorder> list);
    public void save(Backorder bo);
//    public void update(Backorder bo);
    List<Backorder> findCompleted();
    public List<Backorder> findNotCompleted();
    public List<Backorder> findAll();
    public List<BackOrderReportEntry> report(int status);
    public List<Backorder> findByInvoiceNumber(String invoice_number);
    public List<Backorder> findByCustomName(String customerName);
    public List<Backorder> findByStatus(int status);
    public Backorder findById(int id);

    public void processInvoice(Invoice invoice);
    public void stockBackorders(Invoice invoice);
    public void delete(Backorder bo);



}
