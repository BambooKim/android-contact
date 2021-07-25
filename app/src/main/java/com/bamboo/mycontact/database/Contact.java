package com.bamboo.mycontact.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name, mobile;
    // private int imageRes;

    public Contact(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
    }

    /*
    public Contact(String name, String mobile, int imageRes) {
        this.name = name;
        this.mobile = mobile;
        this.imageRes = imageRes;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
