package com.example.wando.daystarlaptops.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.example.wando.daystarlaptops.database.DatabaseRepository;
import com.example.wando.daystarlaptops.database.entity.Item;
import com.example.wando.daystarlaptops.database.entity.Owner;

import java.util.List;

public class ManualSignInViewModel extends AndroidViewModel {

    private DatabaseRepository dbRepo;

    public ManualSignInViewModel(Application application) {
        super(application);
        this.dbRepo = new DatabaseRepository(application);
    }

    public DatabaseRepository getDbRepo() {
        return dbRepo;
    }

    public void insertOwners(List<Owner> owners)
    {
        dbRepo.insertOwners(owners);
    }

    public void insertItems(List<Item> item)
    {
        dbRepo.insertItems(item);
    }

    public List<Item> getAllItems() { return dbRepo.getAllItems(); }

    public List<Owner> getAllOwners() { return dbRepo.getAllOwners(); }
}
