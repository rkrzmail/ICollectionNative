package com.icollection.modelservice;

/**
 * Created by Mounzer on 7/7/2017.
 */
public class MenuItems {
    private String icon;
    private String title;

    public MenuItems() {
    }

    public MenuItems(String title) {
        this.title = title;
    }

    public MenuItems(String icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
