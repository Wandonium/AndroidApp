package com.example.wando.daystarlaptops.database.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Locations")
public class Location {

    @PrimaryKey @NonNull
    private String id;
    @NonNull
    private String location_name;
    @NonNull
    private String campus;

    public Location(String id, String location_name, String campus) {
        this.id = id;
        this.location_name = location_name;
        this.campus = campus;
    }

    @Ignore
    public Location() {
    }

    @Ignore
    public Location(Location other) {
        this.id = other.id;
        this.location_name = other.location_name;
        this.campus = other.campus;
    }

    public String getId() {
        return id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public String getCampus() {
        return campus;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setLocation_name(@NonNull String location_name) {
        this.location_name = location_name;
    }

    public void setCampus(@NonNull String campus) {
        this.campus = campus;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id='" + id + '\'' +
                ", location_name='" + location_name + '\'' +
                ", campus='" + campus + '\'' +
                '}';
    }
}
