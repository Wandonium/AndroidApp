package com.example.wando.daystarlaptops.database.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "Owners"/*,
        foreignKeys = @ForeignKey(entity = Location.class,
                parentColumns = "id",
                childColumns = "location_id",
                onDelete = CASCADE),
        indices = @Index(value = "location_id")*/)
public class Owner implements Serializable {

    @PrimaryKey @NonNull
    private String id;
    private String name;
    private String location_id;

    public Owner(String id, String name, String location_id) {

        this.id = id;
        this.name = name;
        this.location_id = location_id;
    }

    @Ignore
    public Owner() {
    }

    @Ignore
    public Owner(Owner other) {
        this.id = other.id;
        this.name = other.name;
        this.location_id = other.location_id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location_id='" + location_id + '\'' +
                '}';
    }
}
