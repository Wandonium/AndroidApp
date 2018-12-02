package com.example.wando.daystarlaptops.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "CheckIns"/*,
        foreignKeys = {
        @ForeignKey(
                entity = Admin.class,
                parentColumns = "id",
                childColumns = "admin_id"
        ),
        @ForeignKey(
                entity = Item.class,
                parentColumns = "id",
                childColumns = "item_id"
        ),
        @ForeignKey(
                entity = Owner.class,
                parentColumns = "id",
                childColumns = "owner_id"
        )},
        indices = {
                @Index(value = "admin_id"),
                @Index(value = "item_id"),
                @Index(value = "owner_id")
        }*/)
public class CheckIn {

    @PrimaryKey @NonNull
    private String id;
    @NonNull
    private String time_in;
    private String time_out;
    private String location_id;
    private String admin_id;
    private String item_id;
    private String owner_id;
    private String checkin_date;

    public CheckIn(@NonNull String id, @NonNull String time_in, String time_out, String location_id, String admin_id, String item_id, String owner_id, String checkin_date) {
        this.id = id;
        this.time_in = time_in;
        this.time_out = time_out;
        this.location_id = location_id;
        this.admin_id = admin_id;
        this.item_id = item_id;
        this.owner_id = owner_id;
        this.checkin_date = checkin_date;
    }

    public CheckIn(CheckIn other) {
        this.id = other.id;
        this.time_in = other.time_in;
        this.time_out = other.time_out;
        this.location_id = other.location_id;
        this.admin_id = other.admin_id;
        this.item_id = other.item_id;
        this.owner_id = other.owner_id;
        this.checkin_date = other.checkin_date;
    }

    public CheckIn() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getTime_in() {
        return time_in;
    }

    public void setTime_in(@NonNull String time_in) {
        this.time_in = time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getCheckin_date() {
        return checkin_date;
    }

    public void setCheckin_date(String checkin_date) {
        this.checkin_date = checkin_date;
    }

    @Override
    public String toString() {
        return "CheckIn{" +
                "id='" + id + '\'' +
                ", time_in='" + time_in + '\'' +
                ", time_out='" + time_out + '\'' +
                ", location_id='" + location_id + '\'' +
                ", admin_id='" + admin_id + '\'' +
                ", item_id='" + item_id + '\'' +
                ", owner_id='" + owner_id + '\'' +
                ", checkin_date='" + checkin_date + '\'' +
                '}';
    }
}
