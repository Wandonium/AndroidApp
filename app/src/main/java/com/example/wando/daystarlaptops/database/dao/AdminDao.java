package com.example.wando.daystarlaptops.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.wando.daystarlaptops.database.entity.Admin;

import java.util.List;

@Dao
public interface AdminDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Admin... admin);

    @Update
    void update(Admin... admin);

    @Delete
    void delete(Admin... admin);

    @Query("DELETE from Admins")
    void deleteAll();

    @Query("SELECT * FROM Admins")
    List<Admin> getAllAdmins();

    @Query("SELECT * FROM Admins WHERE role =:role")
    LiveData<List<Admin>> findAdminForRole(final String role);

    @Query("SELECT * FROM Admins WHERE location_id =:location_id")
    LiveData<List<Admin>> findAdminForLocation(final String location_id);
}
