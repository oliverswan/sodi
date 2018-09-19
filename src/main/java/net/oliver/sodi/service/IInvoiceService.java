package net.oliver.sodi.service;

import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoicesResult;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IInvoiceService {

    void save(Invoice invoice);

    void saveInvoices(List<Invoice> list);

    List<Invoice> findAll();

    InvoicesResult findAll(PageRequest request);



    List<Invoice> findDraft();

    Invoice findById(int id);
}
