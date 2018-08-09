package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IItemDao;
import net.oliver.sodi.model.Item;
import net.oliver.sodi.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemSericeImpl implements IItemService {

    @Autowired
    private IItemDao dao;

    @Override
    public List<Item> findAll() {return dao.findAll(); }
}
