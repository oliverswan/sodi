package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IContactDao;
import net.oliver.sodi.model.Contact;
import net.oliver.sodi.service.IContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class ContactServiceImpl implements IContactService {

    @Autowired
    IContactDao dao;

    @Autowired
    MongoTemplate mongoTemplate;

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

    @Override
    public List<Contact> findContactAutoComplete(String criteria) {

        Pattern pattern = Pattern.compile("^.*"+criteria+".*$", Pattern.CASE_INSENSITIVE);
        Criteria c1 = Criteria.where("contactName").regex(pattern);
//        Criteria c2 = Criteria.where("name").regex(pattern);
        Query query = new Query(c1);
        return mongoTemplate.find(query, Contact.class);
    }
}
