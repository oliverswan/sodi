package net.oliver.sodi.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OrderItem {

    private String code;
    private Integer quantity;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
