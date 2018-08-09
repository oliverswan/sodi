package net.oliver.sodi.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Document
public class Item {

    @Indexed
    private int id;  //自定义id

    private String code;
    private int soldThisYear;// sold
    private int stock;// 当前存货
    private List<Integer> soldHistory;
    private double spm;// 平均每月销售量
    private double msoh;

    public synchronized void addSold(int s)
    {
        this.soldThisYear += s;
        this.stock -=s;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSoldThisYear() {
        return soldThisYear;
    }

    public void setSoldThisYear(int soldThisYear) {
        this.soldThisYear = soldThisYear;
    }

    public List<Integer> getSoldHistory() {
        return soldHistory;
    }

    public void setSoldHistory(List<Integer> soldHistory) {
        this.soldHistory = soldHistory;
    }

    public double getMonthStockOnHand() {
        return msoh;
    }
    public void setMonthStockOnHand(double monthStockOnHand) {
        this.msoh = monthStockOnHand;
    }
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getSalesPerMonth() {
        return spm;
    }

    public void setSalesPerMonth(double salesPerMonth) {
        this.spm = salesPerMonth;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
