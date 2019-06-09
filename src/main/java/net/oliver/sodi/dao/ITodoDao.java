package net.oliver.sodi.dao;

import net.oliver.sodi.model.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ITodoDao extends MongoRepository<Todo, Integer> {

}
