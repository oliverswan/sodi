package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IOrderDao;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoicesResult;
import net.oliver.sodi.model.Order;
import net.oliver.sodi.model.OrderResult;
import net.oliver.sodi.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    IOrderDao dao;

    @Override
    public List<Order> findAll() {
        return dao.findAll();
    }

    @Override
    public void save(List<Order> orders) {
        dao.save(orders);
    }

    @Override
    public void save(Order order) {
        dao.save(order);
    }


    @Override
    public Order findById(int id) {
        return dao.findOne(id);
    }

    @Override
    public OrderResult findAll(PageRequest request) {
        OrderResult result = new OrderResult();
        List<Order> r3 = new ArrayList<Order>();
        //TODO 分析如何将Page转为List
        Page<Order> result2 =  dao.findAll(request);
        result.setMessage(String.valueOf(result2.getTotalElements()));
        result2.forEach(new Consumer<Order>() {
            @Override
            public void accept(Order invoice) {
                r3.add(invoice);
            }
        });
        result.setData(r3);
        return result;
    }
}
