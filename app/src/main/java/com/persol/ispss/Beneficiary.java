package com.persol.ispss;

import java.util.Date;
import java.util.UUID;

public class Beneficiary {

    private String id;
    private String firstName;
    private String lastName;
    private Date dob;
    private String phone;
    private String relationship;
    private double percentage;
    private String gender;
    private boolean expanded;

    public Beneficiary(String id, String firstName, String lastName, Date dob, String phone, String relationship, double percentage, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.phone = phone;
        this.relationship = relationship;
        this.percentage = percentage;
        this.gender = gender;
    }

    public Beneficiary(String id, String firstName, String lastName, double percentage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.percentage = percentage;
        this.dob = new Date();
        this.phone = "0";
        this.relationship = "";
        this.gender = "Male";
    }

    public Beneficiary(String firstName, String lastName, Date dob, String phone, String relationship, double percentage, String gender) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.phone = phone;
        this.relationship = relationship;
        this.percentage = percentage;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
