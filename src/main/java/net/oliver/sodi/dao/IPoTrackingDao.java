package net.oliver.sodi.dao;

import net.oliver.sodi.model.PoTracking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IPoTrackingDao  extends MongoRepository<PoTracking, Integer> {

    List<PoTracking> findById(int id);
}
