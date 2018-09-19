package net.oliver.sodi.model;

import java.util.List;

public class InvoicesResult {
    private List<Invoice> data;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Invoice> getData() {
        return data;
    }

    public void setData(List<Invoice> data) {
        this.data = data;
    }
}
