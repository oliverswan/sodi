package net.oliver.sodi.model;

import net.oliver.sodi.util.MathUtil;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document
public class Invoice {

    @Indexed
    private int id;  //自定义id
    private String contactName;
    private String contactPerson;
    private String emailAddress;
    private String POAddressLine1;
    private String POAddressLine2;
    private String POAddressLine3;
    private String POAddressLine4;
    private String POCity;
    private String PORegion;
    private String POPostalCode;
    private String POCountry;
    private String InvoiceNumber;
    private String Reference;
    private String orderNumber;
    private String InvoiceDate;
    private String DueDate;
    private int status;/* 0 draft 1 approved 2 imported 3 sent to client*/
    private List<InvoiceItem> items = new ArrayList<InvoiceItem>();
    private BigDecimal totalamount;
    private String customerNote;
    private BigDecimal subtotal = new BigDecimal("0.00");
    private BigDecimal gst = new BigDecimal("0.00");
    private String orderNote;
    private String moblie;
    private String tel;
    private String subtotals;
    private String gsts;
    private String totalamounts;

    public String getSubtotals() {
        return subtotals;
    }

    public void setSubtotals(String subtotals) {
        this.subtotals = subtotals;
    }

    public String getGsts() {
        return gsts;
    }

    public void setGsts(String gsts) {
        this.gsts = gsts;
    }

    public String getTotalamounts() {
        return totalamounts;
    }

    public void setTotalamounts(String totalamounts) {
        this.totalamounts = totalamounts;
    }

    public String getMoblie() {
        return moblie;
    }

    public void setMoblie(String moblie) {
        this.moblie = moblie;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public void reCalculate(){
        this.totalamount = new BigDecimal("0.00");
        for(InvoiceItem item : this.items)
        {
            this.totalamount = this.totalamount.add(item.getTotalamount());
        }
        this.totalamount =  this.totalamount.setScale(2,   BigDecimal.ROUND_HALF_UP);
        this.totalamounts = MathUtil.df.format(this.totalamount);

        this.gst = this.totalamount.multiply(new BigDecimal("0.1"));
        this.gsts =  MathUtil.df.format(this.gst);

        this.subtotal = this.totalamount.add(this.gst);
        this.subtotals = MathUtil.df.format(this.subtotal);
    }
    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getGst() {
        return gst;
    }

    public void setGst(BigDecimal gst) {
        this.gst = gst;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public BigDecimal getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(BigDecimal totalamount) {
        this.totalamount = totalamount;
    }


    public void addItem(InvoiceItem item){
        this.items.add(item);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPOAddressLine1() {
        return POAddressLine1;
    }

    public void setPOAddressLine1(String POAddressLine1) {
        this.POAddressLine1 = POAddressLine1;
    }

    public String getPOAddressLine2() {
        return POAddressLine2;
    }

    public void setPOAddressLine2(String POAddressLine2) {
        this.POAddressLine2 = POAddressLine2;
    }

    public String getPOAddressLine3() {
        return POAddressLine3;
    }

    public void setPOAddressLine3(String POAddressLine3) {
        this.POAddressLine3 = POAddressLine3;
    }

    public String getPOAddressLine4() {
        return POAddressLine4;
    }

    public void setPOAddressLine4(String POAddressLine4) {
        this.POAddressLine4 = POAddressLine4;
    }

    public String getPOCity() {
        return POCity;
    }

    public void setPOCity(String POCity) {
        this.POCity = POCity;
    }

    public String getPORegion() {
        return PORegion;
    }

    public void setPORegion(String PORegion) {
        this.PORegion = PORegion;
    }

    public String getPOPostalCode() {
        return POPostalCode;
    }

    public void setPOPostalCode(String POPostalCode) {
        this.POPostalCode = POPostalCode;
    }

    public String getPOCountry() {
        return POCountry;
    }

    public void setPOCountry(String POCountry) {
        this.POCountry = POCountry;
    }

    public String getInvoiceNumber() {
        return InvoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        InvoiceNumber = invoiceNumber;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String reference) {
        Reference = reference;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    //    @Override
//    public String toString() {
//        return "Invoice{" +
//                "id='" + id + '\'' +
//                ", contactName='" + contactName + '\'' +
//                ", items=" + items.size() +'}';
//    }
}
