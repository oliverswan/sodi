package net.oliver.sodi.service;

import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoicesResult;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IInvoiceService {

    void save(Invoice invoice);
    void update(Invoice invoice);
    void saveInvoices(List<Invoice> list);

    List<Invoice> findAll();

    InvoicesResult findAll(PageRequest request);

    InvoicesResult findByStatus(PageRequest request);

    List<Invoice> findDraft();

    Invoice findById(int id);
    Invoice findByOrderNumber(String number);

    List<Invoice> findLikeNameOrNumber(String customerName,String invoiceNumber);

}
