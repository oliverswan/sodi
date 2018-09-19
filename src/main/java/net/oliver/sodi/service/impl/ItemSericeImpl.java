package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IItemDao;
import net.oliver.sodi.model.Item;
import net.oliver.sodi.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class ItemSericeImpl implements IItemService {

    @Autowired
    private IItemDao dao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void save(Item item) {
        dao.save(item);
    }

    @Override
    public List<Item> findAll() {return dao.findAll(); }

    @Override
    public List<Item> findByCode(String code) {

//        Query query = new Query();
//        query.addCriteria(Criteria.where("code").is(code));
//        List<Item> items = mongoTemplate.find(query, Item.class);

        return dao.findByCode(code);
    }

    @Override
    public void save(List<Item> list) {
        dao.save(list);
    }

    @Override
    public List<Item> findAllForReorder(int month) {
        Query query = new Query();
        query.addCriteria(Criteria.where("msoh").lt(month));//.gt(20));
        return mongoTemplate.find(query,Item.class);
    }

    @Override
    public List<Item> findItemAutoComplete(String criteria) {


        Pattern pattern = Pattern.compile("^.*"+criteria+".*$", Pattern.CASE_INSENSITIVE);
        Criteria c1 = Criteria.where("code").regex(pattern);
        Criteria c2 = Criteria.where("name").regex(pattern);
        Query query = new Query(new Criteria().orOperator(c1,c2));
        return mongoTemplate.find(query,Item.class);
    }

    @Override
    public List<Item> findAllOrderBySoldThisYear() {

//        return dao.findAllOrderBySoldThisYear();
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "soldThisYear"));
        return  mongoTemplate.find(query, Item.class);
    }


    @Override
    public List<Item> findBySoldThisYear(int number) {
        return dao.findBySoldThisYear(number);
    }
}
