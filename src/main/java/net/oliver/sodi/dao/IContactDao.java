package net.oliver.sodi.dao;

import net.oliver.sodi.model.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IContactDao  extends MongoRepository<Contact, Integer> {
    List<Contact> findByContactName(String contactName);
}
