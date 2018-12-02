package com.example.wando.daystarlaptops.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.wando.daystarlaptops.database.DatabaseRepository;
import com.example.wando.daystarlaptops.database.entity.Admin;
import com.example.wando.daystarlaptops.database.entity.Item;
import com.example.wando.daystarlaptops.database.entity.Owner;

import java.util.List;

public class OwnerItemsViewModel extends AndroidViewModel {

    private DatabaseRepository dbRepo;
    private Owner owner;
    private List<Item> ownerItems;

    public OwnerItemsViewModel(@NonNull Application application) {
        super(application);
        dbRepo = new DatabaseRepository(application);
        owner = new Owner();
        ownerItems = null;
    }

    public DatabaseRepository getDbRepo() {
        return dbRepo;
    }

    public Owner getOwner(String owner_id)
    {
        owner = dbRepo.getOwner(owner_id);
        return owner;
    }

    public List<Item> getOwnerItems(String owner_id)
    {
        ownerItems = dbRepo.getOwnerItems(owner_id);
        return ownerItems;
    }

    public List<Admin> getAllAdmins() { return dbRepo.getAllAdmins(); }
}
