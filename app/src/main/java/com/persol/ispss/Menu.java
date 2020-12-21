package com.persol.ispss;

public class Menu {

    private String menuTitle;
    private int drawableId;
    private int id;

    public Menu(int id, String menuTitle, int drawableId) {
        this.id = id;
        this.menuTitle = menuTitle;
        this.drawableId = drawableId;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
