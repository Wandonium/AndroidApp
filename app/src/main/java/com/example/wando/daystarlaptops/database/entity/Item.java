package com.example.wando.daystarlaptops.database.entity;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "Items"/*,
foreignKeys = @ForeignKey(entity = Owner.class,
        parentColumns = "id",
        childColumns = "owner_id",
        onDelete = CASCADE),
        indices = {
        @Index(value = {"serial_number"}, unique = true),
        @Index(value = "owner_id")}*/)
public class Item implements Serializable {

    @PrimaryKey @NonNull
    private String id;
    @ColumnInfo(name = "item_type")
    private String item;
    private String make;
    @NonNull
    private String serial_number;
    private String owner_id;
    //@NonNull
    private Date reg_datetime;
    //@NonNull
    private Date reg_date;

    // Constructor used by Room to create Item entries
    public Item(String id, String item, String make, String serial_number, String owner_id, Date reg_datetime, Date reg_date)
    {
        this.id = id;
        this.item = item;
        this.make = make;
        this.serial_number = serial_number;
        this.owner_id = owner_id;
        this.reg_datetime = reg_datetime;
        this.reg_date = reg_date;
    }

    @Ignore
    public Item() {
    }

    @Ignore
    public Item(Item other) {
        this.id = other.id;
        this.item = other.item;
        this.make = other.make;
        this.serial_number = other.serial_number;
        this.owner_id = other.owner_id;
        this.reg_datetime = other.reg_datetime;
        this.reg_date = other.reg_date;
    }

    public String getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public String getMake() {
        return make;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public Date getReg_datetime() {
        return reg_datetime;
    }

    public Date getReg_date() {
        return reg_date;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setSerial_number(@NonNull String serial_number) {
        this.serial_number = serial_number;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public void setReg_datetime(@NonNull Date reg_datetime) {
        this.reg_datetime = reg_datetime;
    }

    public void setReg_date(Date reg_date) {
        this.reg_date = reg_date;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", item='" + item + '\'' +
                ", make='" + make + '\'' +
                ", serial_number='" + serial_number + '\'' +
                ", owner_id='" + owner_id + '\'' +
                ", reg_datetime=" + reg_datetime +
                ", reg_date=" + reg_date +
                '}';
    }
}
