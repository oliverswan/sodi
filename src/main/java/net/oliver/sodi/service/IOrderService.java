package net.oliver.sodi.service;

import net.oliver.sodi.model.InvoicesResult;
import net.oliver.sodi.model.Order;
import net.oliver.sodi.model.OrderResult;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IOrderService {

    public List<Order> findAll();
    public void save(List<Order> orders);
    public void save(Order order);

    OrderResult findAll(PageRequest request);
}
