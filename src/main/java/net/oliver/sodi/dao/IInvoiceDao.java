package net.oliver.sodi.dao;

import net.oliver.sodi.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

// 不需要写实现类，但method命名一定要对应实体类属性
public interface IInvoiceDao extends MongoRepository<Invoice, Integer> {
}
