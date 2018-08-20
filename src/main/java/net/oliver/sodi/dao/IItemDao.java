package net.oliver.sodi.dao;

import net.oliver.sodi.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// 不需要写实现类，但method命名一定要对应实体类属性
public interface IItemDao extends MongoRepository<Item, Integer> {

    List<Item> findByCode(String code);

//    @Query("{ 'name':{'$regex':?2,'$options':'i'}, sales': {'$gte':?1,'$lte':?2}}")
//    public Page<Item> findByCodeAndAgeRange(String name, double ageFrom, double ageTo, Pageable page);

}
