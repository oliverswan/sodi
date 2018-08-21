package net.oliver.sodi.service;

import net.oliver.sodi.model.Contact;

import java.util.List;

public interface IContactService {

    public void save(Contact contact);
    public List<Contact> findAll();
    public List<Contact> findByContactName(String name );
}
