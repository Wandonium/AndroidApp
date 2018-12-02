package com.example.wando.daystarlaptops.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.wando.daystarlaptops.database.entity.ActivityLog;

import java.util.Date;
import java.util.List;

@Dao
public interface ActivityLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ActivityLog... log);

    @Update
    void update(ActivityLog... log);

    @Delete
    void delete(ActivityLog... log);

    @Query("DELETE from ActivityLogs")
    void deleteAll();

    @Query("SELECT * FROM ActivityLogs")
    LiveData<List<ActivityLog>> getAllLogs();

    @Query("SELECT * FROM ActivityLogs WHERE admin_id =:admin_id")
    LiveData<List<ActivityLog>> findLogsForAdmin(final int admin_id);

    @Query("SELECT * FROM ActivityLogs WHERE activity_datetime =:activity_datetime")
    LiveData<List<ActivityLog>> findLogsForDate(final Date activity_datetime);
}
