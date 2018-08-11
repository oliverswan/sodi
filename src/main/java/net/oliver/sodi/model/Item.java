package net.oliver.sodi.model;

import net.oliver.sodi.util.MathUtil;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Item {

    @Indexed
    private int id;  //自定义id

    private String code;
    private String name;
    private int soldThisYear;// sold
    private int stock;// 当前存货
    private List<Integer> soldHistory;
    private double spm;// 平均每月销售量
    private double msoh;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSpm() {
        return spm;
    }

    public void setSpm(double spm) {
        this.spm = spm;
    }

    public double getMsoh() {
        return msoh;
    }

    public void setMsoh(double msoh) {
        this.msoh = msoh;
    }

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
        this.msoh = MathUtil.trimDouble(this.stock/this.spm);
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
