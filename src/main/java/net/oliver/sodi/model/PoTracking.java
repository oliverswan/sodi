package net.oliver.sodi.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class PoTracking {

    @Indexed
    private int id;  //自定义id
    private String customerName;
    private String partsType;
    private String date;
    private String freightType;
    private List<String> proFormaFileUrls;
    private String proFormaNumber;
    private int proformaInXero;//0,1
    private int depositPaymentStatus;//0,1
    private List<String> depositPaymentUrls;
    private String productionDate;
    private int sodiInvoiceInXero;//0,1
    private int proformaCancelled;//0,1
    private int balancePaymentStatus;//0,1
    private List<String> balancePaymentUrls;
    private String dispatchDate;
    private List<String> shippingPreAlertUrls;
    private String deliveryDate;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPartsType() {
        return partsType;
    }

    public void setPartsType(String partsType) {
        this.partsType = partsType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFreightType() {
        return freightType;
    }

    public void setFreightType(String freightType) {
        this.freightType = freightType;
    }

    public List<String> getProFormaFileUrls() {
        return proFormaFileUrls;
    }

    public void setProFormaFileUrls(List<String> proFormaFileUrls) {
        this.proFormaFileUrls = proFormaFileUrls;
    }

    public String getProFormaNumber() {
        return proFormaNumber;
    }

    public void setProFormaNumber(String proFormaNumber) {
        this.proFormaNumber = proFormaNumber;
    }

    public int getProformaInXero() {
        return proformaInXero;
    }

    public void setProformaInXero(int proformaInXero) {
        this.proformaInXero = proformaInXero;
    }

    public int getDepositPaymentStatus() {
        return depositPaymentStatus;
    }

    public void setDepositPaymentStatus(int depositPaymentStatus) {
        this.depositPaymentStatus = depositPaymentStatus;
    }

    public List<String> getDepositPaymentUrls() {
        return depositPaymentUrls;
    }

    public void setDepositPaymentUrls(List<String> depositPaymentUrls) {
        this.depositPaymentUrls = depositPaymentUrls;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public int getSodiInvoiceInXero() {
        return sodiInvoiceInXero;
    }

    public void setSodiInvoiceInXero(int sodiInvoiceInXero) {
        this.sodiInvoiceInXero = sodiInvoiceInXero;
    }

    public int getProformaCancelled() {
        return proformaCancelled;
    }

    public void setProformaCancelled(int proformaCancelled) {
        this.proformaCancelled = proformaCancelled;
    }

    public int getBalancePaymentStatus() {
        return balancePaymentStatus;
    }

    public void setBalancePaymentStatus(int balancePaymentStatus) {
        this.balancePaymentStatus = balancePaymentStatus;
    }

    public List<String> getBalancePaymentUrls() {
        return balancePaymentUrls;
    }

    public void setBalancePaymentUrls(List<String> balancePaymentUrls) {
        this.balancePaymentUrls = balancePaymentUrls;
    }

    public String getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(String dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public List<String> getShippingPreAlertUrls() {
        return shippingPreAlertUrls;
    }

    public void setShippingPreAlertUrls(List<String> shippingPreAlertUrls) {
        this.shippingPreAlertUrls = shippingPreAlertUrls;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
