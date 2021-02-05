package com.persol.ispss;

public class NationalId {

    private String id;
    private String type;
    private String idNumber;

    public NationalId(String id, String type, String idNumber) {
        this.id = id;
        this.type = type;
        this.idNumber = idNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
