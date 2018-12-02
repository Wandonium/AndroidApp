package com.example.wando.daystarlaptops.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "ActivityLogs",
        foreignKeys = @ForeignKey(entity = Admin.class,
                parentColumns = "id",
                childColumns = "admin_id",
                onDelete = CASCADE),
        indices = @Index(value = "admin_id"))
public class ActivityLog {

    @PrimaryKey
    private int id;
    @NonNull
    private String action;
    private int admin_id;
    @NonNull
    private Date activity_datetime;

    public ActivityLog(int id, @NonNull String action, int admin_id, @NonNull Date activity_datetime) {
        this.id = id;
        this.action = action;
        this.admin_id = admin_id;
        this.activity_datetime = activity_datetime;
    }

    @Ignore
    public ActivityLog() {}

    @Ignore
    public ActivityLog(ActivityLog activityLog)
    {
        this.id = activityLog.id;
        this.action = activityLog.action;
        this.admin_id = activityLog.admin_id;
        this.activity_datetime = activityLog.activity_datetime;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getAction() {
        return action;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    @NonNull
    public Date getActivity_datetime() {
        return activity_datetime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAction(@NonNull String action) {
        this.action = action;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public void setActivity_datetime(@NonNull Date activity_datetime) {
        this.activity_datetime = activity_datetime;
    }
}
