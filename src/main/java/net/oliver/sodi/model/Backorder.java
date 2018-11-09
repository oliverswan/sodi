package net.oliver.sodi.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
public class Backorder {



    @Indexed
    private int id;  //自定义id

    private String invoiceNumber;
    private String customName;
    private Map<String,Integer> orders = new HashMap<String,Integer>();
    private int status;/* 0 not complete 1 complete*/

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Map<String, Integer> getOrders() {
        return orders;
    }

    // 返回还没处理完的个数
    public int removeItem(String code,int quantity)
    {
        Integer now = orders.get(code);
        if(now == null )
            return -quantity;

        //  如果这个单子记录5个，要处理7个，那么就要返回剩下的
        // -2 = 5-7 这个单子归0 然后返回 -2
        Integer realnow = now - quantity;
        if(realnow <= 0)
        {
            orders.remove(code);
        }else{
            orders.put(code,realnow);
        }

        return realnow;
    }

    public void setOrders(Map<String, Integer> orders) {
        this.orders = orders;
    }

    public void addItem(String code, Integer quantity)
    {
        int prev = 0;
        if(orders.containsKey(code))
        {
             prev = orders.get(code);
        }
        orders.put(code,quantity+prev);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}
