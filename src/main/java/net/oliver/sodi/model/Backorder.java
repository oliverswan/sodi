package net.oliver.sodi.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
public class Backorder {

    private String invoice_number;
    private Map<String,Integer> orders = new HashMap<String,Integer>();

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
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
}
