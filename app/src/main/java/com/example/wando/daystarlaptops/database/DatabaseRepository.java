package com.example.wando.daystarlaptops.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.wando.daystarlaptops.database.AppDatabase;
import com.example.wando.daystarlaptops.database.dao.AdminDao;
import com.example.wando.daystarlaptops.database.dao.CheckInDao;
import com.example.wando.daystarlaptops.database.dao.ItemDao;
import com.example.wando.daystarlaptops.database.dao.LocationDao;
import com.example.wando.daystarlaptops.database.dao.OwnerDao;
import com.example.wando.daystarlaptops.database.entity.Admin;
import com.example.wando.daystarlaptops.database.entity.CheckIn;
import com.example.wando.daystarlaptops.database.entity.Item;
import com.example.wando.daystarlaptops.database.entity.Location;
import com.example.wando.daystarlaptops.database.entity.Owner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DatabaseRepository {

    private CheckInDao checkInDao;
    private LiveData<List<CheckIn>> checkIns;
    private String[] checkInIDs;
    private OwnerDao ownerDao;
    private ItemDao itemDao;
    private LocationDao locationDao;
    private AdminDao adminDao;
    private Owner mOwner;
    private Item mItem;

    private static final String TAG = DatabaseRepository.class.getName();

    public DatabaseRepository(Application application)
    {
        AppDatabase db = AppDatabase.getDatabase(application);
        checkInDao = db.checkInDao();
        checkIns = checkInDao.getAllCheckIns();
        ownerDao = db.ownerDao();
        itemDao = db.itemDao();
        locationDao = db.locationDao();
        adminDao = db.adminDao();
        mOwner = null;
        mItem = null;
    }

    public LiveData<List<CheckIn>> getCheckIns()
    {
        return checkIns;
    }

    public List<Owner> getAllOwners() {
        List<Owner> theOwners = new ArrayList<>();
        AsyncTask<Void, Void, List<Owner>> task = new AsyncTask<Void, Void, List<Owner>>()  {
            @Override
            protected List<Owner> doInBackground(Void... voids) {
                return ownerDao.getAllOwners();
            }
        }.execute();
        try {
            theOwners = task.get();
        }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return theOwners;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> theAdmins = new ArrayList<>();
        AsyncTask<Void, Void, List<Admin>> task = new AsyncTask<Void, Void, List<Admin>>()  {
            @Override
            protected List<Admin> doInBackground(Void... voids) {
                return adminDao.getAllAdmins();
            }
        }.execute();
        try {
            theAdmins = task.get();
        }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return theAdmins;
    }

    public List<Location> getAllLocations() {
        List<Location> theLocations = new ArrayList<>();
        AsyncTask<Void, Void, List<Location>> task = new AsyncTask<Void, Void, List<Location>>()  {
            @Override
            protected List<Location> doInBackground(Void... voids) {
                return locationDao.getAllLocations();
            }
        }.execute();
        try {
            theLocations = task.get();
        }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return theLocations;
    }

    public List<CheckIn> getAllCheckIns() {
        List<CheckIn> theCheckIns = new ArrayList<>();
        AsyncTask<Void, Void, List<CheckIn>> task = new AsyncTask<Void, Void, List<CheckIn>>()  {
            @Override
            protected List<CheckIn> doInBackground(Void... voids) {
                return checkInDao.getCheckIns();
            }
        }.execute();
        try {
            theCheckIns = task.get();
        }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return theCheckIns;
    }

    public List<Item> getAllItems() {
        List<Item> theItems = new ArrayList<>();
        AsyncTask<Void, Void, List<Item>> task = new AsyncTask<Void, Void, List<Item>>()  {
            @Override
            protected List<Item> doInBackground(Void... voids) {
                return itemDao.getAllItems();
            }
        }.execute();
        try {
            theItems = task.get();
        }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return theItems;
    }

    public String[] getCheckInIDs() {
        AsyncTask<Void, Void, String[]> task = new AsyncTask<Void, Void, String[]>() {
            @Override
            protected String[] doInBackground(Void... voids) {
                return checkInDao.getAllIDs();
            }
        }.execute();
        try { checkInIDs = task.get();}
        catch(ExecutionException ei) { ei.printStackTrace();}
        catch(InterruptedException ei) { ei.printStackTrace();}
        return checkInIDs;
    }

    public List<CheckIn> getCheckInsForOwner(final String owner_id) {
        List<CheckIn> checkInsForOwner = new ArrayList<>();
        AsyncTask<String, Void, List<CheckIn>> task = new AsyncTask<String, Void, List<CheckIn>>() {
            @Override
            protected List<CheckIn> doInBackground(String... strings) {
                return checkInDao.findCheckInForOwner(strings[0]);
            }
        }.execute(owner_id);
        try { checkInsForOwner = task.get();}
        catch(ExecutionException ei) { ei.printStackTrace();}
        catch(InterruptedException ei) { ei.printStackTrace();}
        return checkInsForOwner;
    }

    public CheckIn getCheckInForItem(final String item_id) {
        CheckIn checkInForItem = null;
        AsyncTask<String, Void, CheckIn> task = new AsyncTask<String, Void, CheckIn>() {
            @Override
            protected CheckIn doInBackground(String... strings) {
                return checkInDao.findCheckInForItem(strings[0]);
            }
        }.execute(item_id);
        try { checkInForItem = task.get();}
        catch(ExecutionException ei) { ei.printStackTrace();}
        catch(InterruptedException ei) { ei.printStackTrace();}
        return checkInForItem;
    }

    public Owner getOwner(final String owner_id) {
        AsyncTask<String, Void, Owner> task = new AsyncTask<String, Void, Owner>() {
            @Override
            protected Owner doInBackground(String... strings) {
                return ownerDao.findOwnerById(strings[0]);
            }
        }.execute(owner_id);
        try { mOwner = task.get();}
        catch(ExecutionException ei) { ei.printStackTrace();}
        catch(InterruptedException ei) { ei.printStackTrace();}
        return mOwner;
    }

    public Item getItem(String item_id) {
        AsyncTask<String, Void, Item> task = new AsyncTask<String, Void, Item>() {
            @Override
            protected Item doInBackground(String... strings) {
                return itemDao.findItemById(strings[0]);
            }
        }.execute(item_id);
        try { mItem = task.get();}
        catch(ExecutionException ei) { ei.printStackTrace();}
        catch(InterruptedException ei) { ei.printStackTrace();}
        return mItem;
    }

    public List<Item> getOwnerItems(String owner_id) {
        List<Item> theItems = new ArrayList<>();
        AsyncTask<String, Void, List<Item>> task = new AsyncTask<String, Void, List<Item>>() {
            @Override
            protected List<Item> doInBackground(String... strings) {
                return itemDao.findItemsForOwner(strings[0]);
            }
        }.execute(owner_id);
        try { theItems = task.get();}
        catch(ExecutionException e) { e.printStackTrace();}
        catch (InterruptedException e) { e.printStackTrace();}
        return theItems;
    }

    public void deleteCheckInList(final List<CheckIn> checkIn)
    {
        new AsyncTask<List<CheckIn>, Void, Void>() {
            @Override
            protected Void doInBackground(List<CheckIn>... checkIns) {
                checkInDao.deleteCheckIns(checkIns[0]);
                return null;
            }
        }.execute(checkIn);
    }

    public void deleteCheckIn(final CheckIn checkIn)
    {
        new AsyncTask<CheckIn, Void, Void>() {
            @Override
            protected Void doInBackground(CheckIn... checkIns) {
                checkInDao.delete(checkIns[0]);
                return null;
            }
        }.execute(checkIn);
    }

    public void insertOwners(List<Owner> owners)
    {
        new AsyncTask<List<Owner>, Void, Void> () {
            @Override
            protected Void doInBackground(List<Owner>... owners) {
                ownerDao.insertAllOwners(owners[0]);
                Log.d(TAG, ": Inserted a list of Owners into DB.");
                return null;
            }
        }.execute(owners);
    }


    public void insertItems(List<Item> items)
    {
        new AsyncTask<List<Item>, Void, Void>() {
            @Override
            protected Void doInBackground(List<Item>... items) {
                itemDao.insertAllItems(items[0]);
                Log.d(TAG, ": Inserted a list of Items into DB.");
                return null;
            }
        }.execute(items);
    }

    public void insertLocation(final Location location)
    {
        new AsyncTask<Location, Void, Void>() {
            @Override
            protected Void doInBackground(Location... locations) {
                locationDao.insert(locations[0]);
                Log.d(TAG, ": Inserted a Locaton into DB.");
                return null;
            }
        }.execute(location);
    }

    public void insertAdmin(final Admin admin)
    {
        new AsyncTask<Admin, Void, Void>() {
            @Override
            protected Void doInBackground(Admin... admins) {
                adminDao.insert(admins[0]);
                Log.d(TAG, ": Inserted an Admin into DB.");
                return null;
            }
        }.execute(admin);
    }

    public void insertCheckInList(List<CheckIn> checkIns)
    {
        new AsyncTask<List<CheckIn>, Void, Void>() {
            @Override
            protected Void doInBackground(List<CheckIn>... checkIns) {
                checkInDao.insertCheckInList(checkIns[0]);
                Log.d(TAG, ": Inserted a list of CheckIns into DB.");
                return null;
            }
        }.execute(checkIns);
    }

    public void insert(CheckIn checkIn)
    {
        new insertAsyncTask(checkInDao).execute(checkIn);
    }

    public static class insertAsyncTask extends AsyncTask<CheckIn, Void, Void>
    {
        private CheckInDao mAsyncTaskDao;

        insertAsyncTask(CheckInDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CheckIn... checkIns) {
            mAsyncTaskDao.insert(checkIns[0]);
            return null;
        }
    }
}
