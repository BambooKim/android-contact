package com.bamboo.mycontact.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM Contact")
    List<Contact> getAll();

    @Query("SELECT * FROM Contact where name LIKE :name")
    List<Contact> searchByName(String name);

    @Query("SELECT * FROM Contact WHERE mobile LIKE :mobile")
    List<Contact> searchByPhone(String mobile);

    @Query("DELETE FROM Contact where id=:contactId")
    void deleteById(int contactId);

    @Query("UPDATE Contact " +
            "SET name=:contactName, mobile=:contactMobile, profile_image=:contactProfileImage " +
            "WHERE id=:contactId")
    void updateById(int contactId, String contactName, String contactMobile, byte[] contactProfileImage);

    @Insert
    void insert(Contact contact);

    @Delete
    void delete(Contact contact);

    @Update
    void update(Contact contact);
}
