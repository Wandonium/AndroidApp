package com.example.wando.daystarlaptops.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.wando.daystarlaptops.database.DatabaseRepository;
import com.example.wando.daystarlaptops.database.entity.Admin;
import com.example.wando.daystarlaptops.database.entity.Location;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {

    private DatabaseRepository dbRepo;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.dbRepo = new DatabaseRepository(application);
    }

    public void insertLocation(Location location) { dbRepo.insertLocation(location);}

    public void insertAdmin(Admin admin) { dbRepo.insertAdmin(admin);}

    public List<Location> getAllLocations() { return dbRepo.getAllLocations(); }

    public List<Admin> getAllAdmins() { return dbRepo.getAllAdmins(); }
}
