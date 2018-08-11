package net.oliver.sodi.service;

import net.oliver.sodi.model.Invoice;

import java.util.List;

public interface IInvoiceService {

    void save(Invoice invoice);

    void saveInvoices(List<Invoice> list);

    List<Invoice> findAll();

    List<Invoice> findDraft();
}
