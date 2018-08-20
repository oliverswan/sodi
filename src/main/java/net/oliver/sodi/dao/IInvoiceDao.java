package net.oliver.sodi.dao;

import net.oliver.sodi.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// 不需要写实现类，但method命名一定要对应实体类属性
public interface IInvoiceDao extends MongoRepository<Invoice, Integer> {

    List<Invoice> findByStatus(int status);




//    @Query("{ 'name':{'$regex':?2,'$options':'i'}, sales': {'$gte':?1,'$lte':?2}}")
//    public Page<Product> findByNameAndAgeRange(String name,double ageFrom,double ageTo,Pageable page);
//
//    注释Query里面的就是mongodb原来的查询语法，我们可以定义传进来的查询参数，通过坐标定义方法的参数。
//
//    还可以在后面指定要返回的数据字段，如上面的例子修改如下，则只通过person表里面的name和age字段构建person对象。
//
//    @Query(value="{ 'name':{'$regex':?2,'$options':'i'}, sales':{'$gte':?1,'$lte':?2}}",fields="{ 'name' : 1, 'age' : 1}")
//    public Page<Product> findByNameAndAgeRange(String name,double ageFrom,double ageTo,Pageable page);
}
