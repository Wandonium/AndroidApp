package com.example.wando.daystarlaptops.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.wando.daystarlaptops.database.DatabaseRepository;
import com.example.wando.daystarlaptops.database.entity.CheckIn;
import com.example.wando.daystarlaptops.database.entity.Item;
import com.example.wando.daystarlaptops.database.entity.Owner;

import java.util.List;

public class CheckInViewModel extends AndroidViewModel {

    private DatabaseRepository mRepository;
    private LiveData<List<CheckIn>> checkIns;

    public CheckInViewModel(Application application) {
        super(application);
        mRepository = new DatabaseRepository(application);
        checkIns = mRepository.getCheckIns();
    }

    public LiveData<List<CheckIn>> getCheckIns() { return checkIns;}

    public List<CheckIn> getAllCheckIns() { return mRepository.getAllCheckIns(); }

    public String[] getCheckInIDs() { return mRepository.getCheckInIDs();}

    public List<CheckIn> getCheckInsForOwner(String owner_id) { return mRepository.getCheckInsForOwner(owner_id);}

    public CheckIn getCheckInForItem(String item_id) { return mRepository.getCheckInForItem(item_id);}

    public void deleteCheckIn(CheckIn checkIn) { mRepository.deleteCheckIn(checkIn);}

    public void deleteCheckInList(List<CheckIn> checkIns) { mRepository.deleteCheckInList(checkIns);}

    public void insertCheckInList(List<CheckIn> checkIns) { mRepository.insertCheckInList(checkIns);}

    public String getOwnerName(CheckIn checkIn)
    {
        String owner_id = checkIn.getOwner_id();
        Owner mOwner = mRepository.getOwner(owner_id);
        String owner_name = mOwner.getName();
        return owner_name;
    }

    public String getItemType(CheckIn checkIn)
    {
        String item_id = checkIn.getItem_id();
        Item mItem = mRepository.getItem(item_id);
        String itemType = mItem.getItem();
        return itemType;
    }

    public void insert(CheckIn checkIn) { mRepository.insert(checkIn);}
}
