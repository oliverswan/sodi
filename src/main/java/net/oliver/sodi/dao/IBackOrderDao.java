package net.oliver.sodi.dao;

import net.oliver.sodi.model.Backorder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IBackOrderDao extends MongoRepository<Backorder, Integer> {
}
