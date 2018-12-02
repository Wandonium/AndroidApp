package com.example.wando.daystarlaptops.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.wando.daystarlaptops.database.entity.Item;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllItems(List<Item> items);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("DELETE from Items")
    void deleteAll();

    @Query("SELECT * FROM Items")
    List<Item> getAllItems();

    @Query("SELECT * FROM Items WHERE serial_number =:serial_number")
    LiveData<List<Item>> findItemBySerialNumber(final String serial_number);

    @Query("SELECT * FROM Items WHERE owner_id =:owner_id")
    List<Item> findItemsForOwner(final String owner_id);

    @Query("SELECT * FROM Items WHERE id =:item_id")
    Item findItemById(final String item_id);
}
