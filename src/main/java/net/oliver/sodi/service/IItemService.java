package net.oliver.sodi.service;

import net.oliver.sodi.model.Item;

import java.util.List;

public interface IItemService {

    void save(Item item);
    List<Item> findAll();
    List<Item> findByCode(String code);
    void save(List<Item> list);
    List<Item> findAllForReorder(int month);
}
