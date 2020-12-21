package com.persol.ispss;

public class MomoAccount {

    private String momoProvider;
    private String momoNumber;
    private String momoName;

    public MomoAccount(String momoProvider, String momoNumber, String momoName) {
        this.momoProvider = momoProvider;
        this.momoNumber = momoNumber;
        this.momoName = momoName;
    }

    public String getMomoProvider() {
        return momoProvider;
    }

    public void setMomoProvider(String momoProvider) {
        this.momoProvider = momoProvider;
    }

    public String getMomoNumber() {
        return momoNumber;
    }

    public void setMomoNumber(String momoNumber) {
        this.momoNumber = momoNumber;
    }

    public String getMomoName() {
        return momoName;
    }

    public void setMomoName(String momoName) {
        this.momoName = momoName;
    }
}
