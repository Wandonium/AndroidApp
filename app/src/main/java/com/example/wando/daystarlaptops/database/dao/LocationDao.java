package com.example.wando.daystarlaptops.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.wando.daystarlaptops.database.entity.Location;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Location... locations);

    @Update
    void update(Location... locations);

    @Delete
    void delete(Location... locations);

    @Query("DELETE from Locations")
    void deleteAll();

    @Query("SELECT * FROM Locations")
    List<Location> getAllLocations();

    @Query("SELECT * FROM Locations WHERE campus =:campus")
    LiveData<List<Location>> findLocationByCampus(final String campus);
}
