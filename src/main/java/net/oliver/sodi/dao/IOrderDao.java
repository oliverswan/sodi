package net.oliver.sodi.dao;


import net.oliver.sodi.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IOrderDao  extends PagingAndSortingRepository<Order, Integer> {


    public Page<Order> findAll(Pageable pageable);

    public List<Order> findAll();
}
