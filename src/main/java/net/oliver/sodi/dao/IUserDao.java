package net.oliver.sodi.dao;

import net.oliver.sodi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IUserDao extends MongoRepository<User, Integer> {

    public User findByName(String name);
}
