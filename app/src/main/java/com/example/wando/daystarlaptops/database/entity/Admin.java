package com.example.wando.daystarlaptops.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "Admins"/*,
        foreignKeys = @ForeignKey(entity = Location.class,
                parentColumns = "id",
                childColumns = "location_id",
                onDelete = CASCADE),
        indices = @Index(value = "location_id")*/)
public class Admin {

    @PrimaryKey @NonNull
    private String id;
    @NonNull
    private String name;
    private String location_id;
    @NonNull
    private String role;
    @NonNull
    private String password;

    public Admin(String id, String name, String location_id, String role, String password) {
        this.id = id;
        this.name = name;
        this.location_id = location_id;
        this.role = role;
        this.password = password;
    }

    @Ignore
    public Admin() {
    }

    @Ignore
    public Admin(Admin other) {
        this.id = other.id;
        this.name = other.name;
        this.location_id = other.location_id;
        this.role = other.role;
        this.password = other.password;
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

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public void setRole(@NonNull String role) {
        this.role = role;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location_id='" + location_id + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
