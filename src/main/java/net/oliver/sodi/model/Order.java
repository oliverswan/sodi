package net.oliver.sodi.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
public class Order {

    @Indexed
    private int id;  //自定义id

    private Map<String,Integer> items = new HashMap<String,Integer>();

    private String name;

    public void addItem(String code,Integer quantity)
    {
        if(this.items.containsKey(code))
        {
            this.items.put(code,this.items.get(code)+quantity);
        }else{
            this.items.put(code,quantity);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
