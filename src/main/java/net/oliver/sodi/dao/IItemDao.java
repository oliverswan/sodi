package net.oliver.sodi.dao;

import net.oliver.sodi.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// 不需要写实现类，但method命名一定要对应实体类属性
public interface IItemDao extends MongoRepository<Item, Integer> {

    List<Item> findByCode(String code);
    List<Item> findAllOrderBySoldThisYear();
    List<Item> findBySoldThisYear(int number);
//    @Query("{ 'name':{'$regex':?2,'$options':'i'}, sales': {'$gte':?1,'$lte':?2}}")
//    public Page<Item> findByCodeAndAgeRange(String name, double ageFrom, double ageTo, Pageable page);
    List<Item> findByMsohGreaterThanEqual(double criteriaValue);
    List<Item> findBySpmGreaterThanEqual(double criteriaValue);
    List<Item> findByMsohLessThanEqual(double criteriaValue);
    List<Item> findBySpmLessThanEqual(double criteriaValue);
    List<Item> findByMsohEquals(double criteriaValue);
    List<Item> findBySpmEquals(double criteriaValue);
}
