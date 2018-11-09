package net.oliver.sodi.dao;

import net.oliver.sodi.model.Backorder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IBackOrderDao extends MongoRepository<Backorder, Integer> {
    List<Backorder> findByInvoiceNumber(String invoice_number);
    List<Backorder> findByStatusGreaterThan(int status);
    List<Backorder> findByStatusLessThan(int status);
    List<Backorder> findByCustomName(String customername);
}
