package com.bamboo.mycontact;

public class ContactItem {
    String name, mobile;
    int resId;

    public ContactItem(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
    }

    public ContactItem(String name, String mobile, int resId) {
        this.name = name;
        this.mobile = mobile;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public int getResId() {
        return resId;
    }
}
