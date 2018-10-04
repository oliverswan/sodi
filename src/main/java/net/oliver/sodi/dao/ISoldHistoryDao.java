package net.oliver.sodi.dao;

import net.oliver.sodi.model.SoldHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ISoldHistoryDao extends MongoRepository<SoldHistory, Integer> {

    List<SoldHistory> findByCode(String code);
}
