package net.oliver.sodi.dao;

import net.oliver.sodi.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

// 不需要写实现类，但method命名一定要对应实体类属性
public interface IItemDao extends MongoRepository<Item, Integer> {

}
