package com.persol.ispss;

import java.util.ArrayList;
import java.util.Date;

public class NewMember {
    private static String firstName;
    private static String middleName;
    private static String lastName;
    private static String email;
    private static String phone;
    private static Date dob;
    private static String gender;
    private static String memberID;
    private static String residence;
    private static String occupation;
    private static String hometown;
    private static double mmc;
    private static BankAccount bankAccount = null;
    private static MomoAccount momoAccount = null;
    private static ArrayList<Beneficiary> beneficiaries = new ArrayList<>();
    private static double appr;
    private static Scheme scheme;
    private static boolean memberExisting;
    private static IDCard nationalId;

    public NewMember() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        NewMember.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        NewMember.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        NewMember.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        NewMember.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        NewMember.phone = phone;
    }

    public Date getDateOfBirth() {
        return dob;
    }

    public void setDateOfBirth(Date dob) {
        NewMember.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        NewMember.gender = gender;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        NewMember.bankAccount = bankAccount;
    }

    public MomoAccount getMomoAccount() {
        return momoAccount;
    }

    public void setMomoAccount(MomoAccount momoAccount) {
        NewMember.momoAccount = momoAccount;
    }

    public  String getMemberID() {
        return memberID;
    }

    public  void setMemberID(String memberID) {
        NewMember.memberID = memberID;
    }

    public  String getResidence() {
        return residence;
    }

    public  void setResidence(String residence) {
        NewMember.residence = residence;
    }

    public  String getOccupation() {
        return occupation;
    }

    public  void setOccupation(String occupation) {
        NewMember.occupation = occupation;
    }

    public  String getHometown() {
        return hometown;
    }

    public  void setHometown(String hometown) {
        NewMember.hometown = hometown;
    }

    public  double getMmc() {
        return mmc;
    }

    public  void setMmc(double mmc) {
        NewMember.mmc = mmc;
    }

    public  double getAppr() {
        return appr;
    }

    public  void setAppr(double appr) {
        NewMember.appr = appr;
    }

    public  ArrayList<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    public  void setBeneficiaries(ArrayList<Beneficiary> beneficiaries) {
        NewMember.beneficiaries = beneficiaries;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(Scheme scheme) {
        NewMember.scheme = scheme;
    }

    public  boolean isMemberExisting() {
        return memberExisting;
    }

    public  void setMemberExisting(boolean memberExisting) {
        NewMember.memberExisting = memberExisting;
    }

    public IDCard getNationalId() {
        return nationalId;
    }

    public void setNationalId(IDCard nationalId) {
        NewMember.nationalId = nationalId;
    }
}