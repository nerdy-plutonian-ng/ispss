package com.persol.ispss;

public class SettingItem {

    private int id;
    private String title;
    private String value;
    private int icon;

    public SettingItem(int id, String title, String value, int icon) {
        this.id = id;
        this.title = title;
        this.value = value;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
