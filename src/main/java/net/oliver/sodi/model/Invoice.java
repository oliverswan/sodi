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
    private String poaddressline1;
    private String poaddressline2;
    private String poaddressline3;
    private String poaddressline4;
    private String pocity;
    private String poregion;
    private String popostalcode;
    private String pocountry;
    private String invoiceNumber;
    private String reference;
    private String orderNumber;
    private String invoiceDate;
    private String dueDate;
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

    public void addItem(InvoiceItem item){
        this.items.add(item);
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

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPoaddressline1() {
        return poaddressline1;
    }

    public void setPoaddressline1(String poaddressline1) {
        this.poaddressline1 = poaddressline1;
    }

    public String getPoaddressline2() {
        return poaddressline2;
    }

    public void setPoaddressline2(String poaddressline2) {
        this.poaddressline2 = poaddressline2;
    }

    public String getPoaddressline3() {
        return poaddressline3;
    }

    public void setPoaddressline3(String poaddressline3) {
        this.poaddressline3 = poaddressline3;
    }

    public String getPoaddressline4() {
        return poaddressline4;
    }

    public void setPoaddressline4(String poaddressline4) {
        this.poaddressline4 = poaddressline4;
    }

    public String getPocity() {
        return pocity;
    }

    public void setPocity(String pocity) {
        this.pocity = pocity;
    }

    public String getPoregion() {
        return poregion;
    }

    public void setPoregion(String poregion) {
        this.poregion = poregion;
    }

    public String getPopostalcode() {
        return popostalcode;
    }

    public void setPopostalcode(String popostalcode) {
        this.popostalcode = popostalcode;
    }

    public String getPocountry() {
        return pocountry;
    }

    public void setPocountry(String pocountry) {
        this.pocountry = pocountry;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(BigDecimal totalamount) {
        this.totalamount = totalamount;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
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

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
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
}
