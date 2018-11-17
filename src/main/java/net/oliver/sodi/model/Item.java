package net.oliver.sodi.model;

import net.oliver.sodi.util.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

@Document
public class Item implements Comparable<Item> {

    @Indexed
    private int id;  //自定义id

    private String code;
    private String name;
    private int soldThisYear;// sold
    private int stock;// 当前存货
    private List<Integer> soldHistory;

    public double getCpriceAu() {
        return cpriceAu;
    }

    public void setCpriceAu(double cpriceAu) {
        this.cpriceAu = cpriceAu;
    }

    public double getSpriceAu() {
        return spriceAu;
    }

    public void setSpriceAu(double spriceAu) {
        this.spriceAu = spriceAu;
    }

    // 需要一个变量指明过去多少个月的
    private double spm;// 平均每月销售量
    private double msoh;
    private double weight;
    private double price;

    private String orderNumber;
    private String accountCode;
    private int coming;
    private String location;

    // 增加对利润的计算
    private double cprice;// public price Eu
    private double sprice;// SKA buy Price Eu
    private double cpriceAu; // Sell price
    private double spriceAu;// Landed price

    private double value;// 库存总价值
    private String profit;// 利润率


    public void updateProfit(double cprice, double sprice, double rate,double freight, double duty)
    {
        if(cprice!=0)
        this.cprice = cprice;
        if(sprice!=0)
        this.sprice = sprice;//Landed price

        this.cpriceAu = MathUtil.trimDouble((this.cprice / rate)*freight*duty);
        this.spriceAu = MathUtil.trimDouble((this.sprice / rate)*freight*duty);

        this.value = MathUtil.trimDouble(this.spriceAu * this.stock);//Landed price
        double p = ((this.cpriceAu - this.price)/this.price)*100;
        this.profit =MathUtil.trimDoubleString(p)  +"%";
    }

    public void reCalValue()
    {
        this.value = MathUtil.trimDouble(this.spriceAu * this.stock);//Landed price
    }

    public double getSprice() {
        return sprice;
    }

    public void setSprice(double sprice) {
        this.sprice = sprice;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}

    public int getComing() {
        return coming;
    }

    public void setComing(int coming) {
        this.coming = coming;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

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

    public void reCalculateBasedOnThisYear(int month){
        double spm2 = (float)this.soldThisYear/month;
        this.spm = MathUtil.trimDouble(spm2);
        if(this.stock <= 0)
            this.msoh =0;
        else if(this.spm <=0)
            this.msoh = this.stock;
        else
            this.msoh = MathUtil.trimDouble(this.stock/this.spm);
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
            double msoh2 = (double)this.stock/this.spm;
            this.msoh = MathUtil.trimDouble(msoh2);
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


    @Override
    public int compareTo(Item o) {
        return this.code.compareTo(o.getCode());
    }
}
