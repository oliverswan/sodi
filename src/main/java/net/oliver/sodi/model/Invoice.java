package net.oliver.sodi.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Invoice {

    @Indexed
    private int id;  //自定义id
    private String contactName;
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
    private String InvoiceDate;
    private String DueDate;
    private int status;/* 0 draft 1 approved 2 imported*/
    private List<InvoiceItem> items = new ArrayList<InvoiceItem>();
    private double totalamount;

    public double getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(double totalamount) {
        this.totalamount = totalamount;
    }
    public void addTotalAmount(double amount)
    {
        this.totalamount +=amount;
    }

    public void addItem(InvoiceItem item){
        this.items.add(item);
        this.addTotalAmount(item.getTotalamount());
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

//    @Override
//    public String toString() {
//        return "Invoice{" +
//                "id='" + id + '\'' +
//                ", contactName='" + contactName + '\'' +
//                ", items=" + items.size() +'}';
//    }
}
