package com.bamboo.mycontact.database;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;

@Entity
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name, mobile;

    @ColumnInfo(name = "profile_image", typeAffinity = ColumnInfo.BLOB)
    private byte[] byteArray;


    public Contact(String name, String mobile, byte[] byteArray) {
        this.name = name;
        this.mobile = mobile;
        this.byteArray = byteArray;
    }

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

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", byteArray=" + Arrays.toString(byteArray) +
                '}';
    }
}
