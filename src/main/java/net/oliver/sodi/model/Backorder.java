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
}
