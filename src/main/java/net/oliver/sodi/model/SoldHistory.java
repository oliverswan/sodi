package net.oliver.sodi.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
public class SoldHistory {

    @Indexed
    private int id;  //自定义id

    private String code;
    private Map<Integer,Integer> his = new HashMap<Integer,Integer>();
    private Map<String,Integer> customerToSold = new HashMap<String,Integer>();

//    public void updateSoldQuantity(Integer month,Integer quantity)
//    {
//        Integer q = this.his.get(month);
//        if(q==null)
//        {
//            q = 0;
//            this.his.put(month,0);
//        }
//        this.his.put(month,q+quantity);
//    }

    public void updateSoldQuantity(String customer ,Integer month,Integer quantity)
    {
        Integer q = this.his.get(month);
        if(q==null)
        {
            q = 0;
            this.his.put(month,0);
        }
        this.his.put(month,q+quantity);

        Integer customerSold = this.customerToSold.get(customer);
        if(customerSold == null)
        {
            customerSold = 0;
            this.customerToSold.put(customer,quantity);
        }
        this.customerToSold.put(customer,customerSold+quantity);

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, Integer> getHis() {
        return his;
    }

    public void setHis(Map<Integer, Integer> his) {
        this.his = his;
    }
}
