package com.example.wando.daystarlaptops.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.wando.daystarlaptops.database.entity.Owner;

import java.util.List;

@Dao
public interface OwnerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Owner owner);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllOwners(List<Owner> owners);

    @Update
    void update(Owner owner);

    @Delete
    void delete(Owner owner);

    @Query("DELETE from Owners")
    void deleteAll();

    @Query("SELECT * FROM Owners")
    List<Owner> getAllOwners();

    @Query("SELECT * FROM Owners WHERE location_id =:location_id")
    LiveData<List<Owner>> findOwnerByLocation(final String location_id);

    @Query("SELECT * FROM Owners WHERE id =:owner_id")
    Owner findOwnerById(final String owner_id);
}
