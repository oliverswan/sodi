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
    private double weight;
    private double price;
    private double cprice;

    public double getCprice() {
        return cprice;
    }
    public void setCprice(double cprice) {
        this.cprice = cprice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
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
        try {
            if(this.spm == 0)
            {
                this.msoh = this.stock;
                return;
            }
            this.msoh = MathUtil.trimDouble(this.stock/this.spm);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

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
