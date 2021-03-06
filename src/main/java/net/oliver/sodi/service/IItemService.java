package net.oliver.sodi.service;

import net.oliver.sodi.model.Item;

import java.util.List;

public interface IItemService {

    void save(Item item);
    List<Item> findAll();
    List<Item> findByCode(String code);
    void save(List<Item> list);
    List<Item> findAllForReorder(int month);

    List<Item> findForDel1();

    List<Item> findItemAutoComplete(String code);
    List<Item> findAllOrderBySoldThisYear();
    List<Item> findBySoldThisYear(int number);

    boolean receiveOneItem(int id);
    void delete(List<Item> l);

    List<Item> findForLandedZero();
}
