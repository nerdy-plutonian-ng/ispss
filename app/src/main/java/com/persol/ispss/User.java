package com.persol.ispss;

import org.json.JSONObject;

import java.util.Date;

public class User {

    private String fName;
    private String mName;
    private String lNAme;
    private String dob;
    private String phone;
    private String email;
    private String gender;
    public static Date dobDate;

    public User(String fName, String mName, String lNAme, String dob, String phone,
                       String email, String gender) {
        this.fName = fName;
        this.mName = mName;
        this.lNAme = lNAme;
        this.dob = dob;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
    }

    public JSONObject getNewUserObject(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstName",fName);
            jsonObject.put("middleName",mName);
            jsonObject.put("lastName",lNAme);
            jsonObject.put("dob",dob);
            jsonObject.put("phoneNumber",phone);
            jsonObject.put("emailAddress",email);
            jsonObject.put("gender",gender);
            return jsonObject;
        } catch (Exception e){
            return null;
        }
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getlNAme() {
        return lNAme;
    }

    public void setlNAme(String lNAme) {
        this.lNAme = lNAme;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
