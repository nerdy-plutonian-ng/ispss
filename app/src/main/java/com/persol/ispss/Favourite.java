package com.persol.ispss;

public class Favourite {

    private String id;
    private String userId;
    private String name;

    public Favourite(String id, String userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Favourite{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
