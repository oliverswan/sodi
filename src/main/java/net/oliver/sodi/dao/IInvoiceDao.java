package net.oliver.sodi.dao;

import net.oliver.sodi.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

// 不需要写实现类，但method命名一定要对应实体类属性
public interface IInvoiceDao extends PagingAndSortingRepository<Invoice, Integer> {

    List<Invoice> findByStatus(int status);

    /*这里注意一点，虽然我们在调用Repository方法中的分页查询时，传入的参数是PageRequest。
    但一定要在Repository定义该方法时参数定义为Pageable。否则会报错:Paging query needs to have a Pageable parameter*/
    public Page<Invoice> findAll(Pageable pageable);

    public List<Invoice> findAll();

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
