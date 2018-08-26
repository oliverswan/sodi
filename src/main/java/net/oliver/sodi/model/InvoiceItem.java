package net.oliver.sodi.model;

import net.oliver.sodi.util.MathUtil;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class InvoiceItem {

    private String InventoryItemCode;
    private String Description;
    private int Quantity;
    private double UnitAmount;
    private String Discount;
    private String AccountCode;
    private String TaxType;
    private String TrackingName1;
    private String TrackingOption1;
    private String TrackingName2;
    private String TrackingOption2;
    private String Currency;
    private String BrandingTheme;
    private String product_attribute;
    private String product_subtotal_discount;
    private String product_quantity;
    private double totalamount;
    private double subtotal;
    private double gst;

    public void reCalculate()
    {
        this.totalamount = MathUtil.trimDouble(this.Quantity * this.UnitAmount);
        this.gst = MathUtil.trimDouble(this.totalamount * 0.1);
        this.subtotal = MathUtil.trimDouble(this.totalamount * 1.1);
    }

    public double getGst() {
        return gst;
    }

    public void setGst(double gst) {
        this.gst = gst;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(double totalamount) {
        this.totalamount = totalamount;
    }

    public String getInventoryItemCode() {
        return InventoryItemCode;
    }

    public void setInventoryItemCode(String inventoryItemCode) {
        InventoryItemCode = inventoryItemCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
        reCalculate();
    }

    public double getUnitAmount() {
        return UnitAmount;
    }

    public void setUnitAmount(double unitAmount) {
        UnitAmount = unitAmount;
        reCalculate();
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getAccountCode() {
        return AccountCode;
    }

    public void setAccountCode(String accountCode) {
        AccountCode = accountCode;
    }

    public String getTaxType() {
        return TaxType;
    }

    public void setTaxType(String taxType) {
        TaxType = taxType;
    }

    public String getTrackingName1() {
        return TrackingName1;
    }

    public void setTrackingName1(String trackingName1) {
        TrackingName1 = trackingName1;
    }

    public String getTrackingOption1() {
        return TrackingOption1;
    }

    public void setTrackingOption1(String trackingOption1) {
        TrackingOption1 = trackingOption1;
    }

    public String getTrackingName2() {
        return TrackingName2;
    }

    public void setTrackingName2(String trackingName2) {
        TrackingName2 = trackingName2;
    }

    public String getTrackingOption2() {
        return TrackingOption2;
    }

    public void setTrackingOption2(String trackingOption2) {
        TrackingOption2 = trackingOption2;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getBrandingTheme() {
        return BrandingTheme;
    }

    public void setBrandingTheme(String brandingTheme) {
        BrandingTheme = brandingTheme;
    }

    public String getProduct_attribute() {
        return product_attribute;
    }

    public void setProduct_attribute(String product_attribute) {
        this.product_attribute = product_attribute;
    }

    public String getProduct_subtotal_discount() {
        return product_subtotal_discount;
    }

    public void setProduct_subtotal_discount(String product_subtotal_discount) {
        this.product_subtotal_discount = product_subtotal_discount;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }
}
