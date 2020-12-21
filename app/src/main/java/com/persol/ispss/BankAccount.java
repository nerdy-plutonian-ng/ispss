package com.persol.ispss;

public class BankAccount {

    private String bankNumber;
    private String bankAccountName;
    private String bankName;
    private String bankBranch;
    private String bankAccountType;

    public BankAccount(String bankNumber, String bankAccountName, String bankName, String bankBranch, String bankAccountType) {
        this.bankNumber = bankNumber;
        this.bankAccountName = bankAccountName;
        this.bankName = bankName;
        this.bankBranch = bankBranch;
        this.bankAccountType = bankAccountType;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }
}
