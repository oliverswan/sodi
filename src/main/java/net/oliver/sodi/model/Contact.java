package net.oliver.sodi.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Contact {
    @Indexed
    private int id;

    private String contactName;
    private String personName;
    private String emailAddress;
    private String poAddressLine1;
    private String poAddressLine2;
    private String poAddressLine3;
    private String poAddressLine4;
    private String poCity;
    private String poRegion;
    private String poPostalCode;
    private String poCountry;
    private String mobile;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPoAddressLine1() {
        return poAddressLine1;
    }

    public void setPoAddressLine1(String poAddressLine1) {
        this.poAddressLine1 = poAddressLine1;
    }

    public String getPoAddressLine2() {
        return poAddressLine2;
    }

    public void setPoAddressLine2(String poAddressLine2) {
        this.poAddressLine2 = poAddressLine2;
    }

    public String getPoAddressLine3() {
        return poAddressLine3;
    }

    public void setPoAddressLine3(String poAddressLine3) {
        this.poAddressLine3 = poAddressLine3;
    }

    public String getPoAddressLine4() {
        return poAddressLine4;
    }

    public void setPoAddressLine4(String poAddressLine4) {
        this.poAddressLine4 = poAddressLine4;
    }

    public String getPoCity() {
        return poCity;
    }

    public void setPoCity(String poCity) {
        this.poCity = poCity;
    }

    public String getPoRegion() {
        return poRegion;
    }

    public void setPoRegion(String poRegion) {
        this.poRegion = poRegion;
    }

    public String getPoPostalCode() {
        return poPostalCode;
    }

    public void setPoPostalCode(String poPostalCode) {
        this.poPostalCode = poPostalCode;
    }

    public String getPoCountry() {
        return poCountry;
    }

    public void setPoCountry(String poCountry) {
        this.poCountry = poCountry;
    }
}
