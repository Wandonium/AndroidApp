package com.example.wando.daystarlaptops.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.wando.daystarlaptops.database.entity.CheckIn;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CheckInDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CheckIn... checkIns);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCheckInList(List<CheckIn> checkIns);

    @Update
    void update(CheckIn... checkIns);

    @Delete
    void delete(CheckIn... checkIns);

    @Delete
    void deleteCheckIns(List<CheckIn> checkIns);

    @Query("DELETE from CheckIns")
    void deleteAll();

    @Query("SELECT * FROM CheckIns")
    LiveData<List<CheckIn>> getAllCheckIns();

    @Query("SELECT * FROM CheckIns")
    List<CheckIn> getCheckIns();

    @Query("SELECT id FROM CheckIns")
    String[] getAllIDs();

    @Query("SELECT * FROM CheckIns WHERE admin_id =:admin_id")
    LiveData<List<CheckIn>> findCheckInByAdmin(final String admin_id);

    @Query("SELECT * FROM CheckIns WHERE item_id =:item_id")
    CheckIn findCheckInForItem(final String item_id);

    @Query("SELECT * FROM CheckIns WHERE owner_id =:owner_id")
    List<CheckIn> findCheckInForOwner(final String owner_id);
}
