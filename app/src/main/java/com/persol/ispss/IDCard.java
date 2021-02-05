package com.persol.ispss;

import java.util.Date;

public class IDCard {

    private String idType;
    private String idNumber;
    private String nameOnCard;
    private Date expiryDate;

    public IDCard(String idType, String idNumber, String nameOnCard, Date expiryDate) {
        this.idType = idType;
        this.idNumber = idNumber;
        this.nameOnCard = nameOnCard;
        this.expiryDate = expiryDate;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}