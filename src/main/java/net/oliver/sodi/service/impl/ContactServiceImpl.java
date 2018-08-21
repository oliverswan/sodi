package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IContactDao;
import net.oliver.sodi.model.Contact;
import net.oliver.sodi.service.IContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements IContactService {

    @Autowired
    IContactDao dao;

    @Override
    public void save(Contact contact) {
        dao.save(contact);
    }

    @Override
    public List<Contact> findAll() {
        return dao.findAll();
    }

    @Override
    public List<Contact> findByContactName(String name) {
        return dao.findByContactName(name);
    }
}
