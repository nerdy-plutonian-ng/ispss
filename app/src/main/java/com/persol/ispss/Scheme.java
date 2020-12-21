package com.persol.ispss;

import java.io.Serializable;
import java.util.Date;

public class Scheme implements Serializable {

    private String id;
    private String name;
    private double percentage;
    private Date startDate;
    private double savings;

    public Scheme(String id, String name, double percentage, Date startDate, double savings) {
        this.id = id;
        this.name = name;
        this.percentage = percentage;
        this.startDate = startDate;
        this.savings = savings;
    }

    public Scheme(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Scheme(String id, double percentage) {
        this.id = id;
        this.percentage = percentage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    public boolean isEqualTo(Scheme otherScheme){
        return getId().equals(otherScheme.getId()) && getName().equals(otherScheme.getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
