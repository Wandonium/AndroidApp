package com.example.wando.daystarlaptops.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.wando.daystarlaptops.database.converter.Converters;
import com.example.wando.daystarlaptops.database.dao.ActivityLogDao;
import com.example.wando.daystarlaptops.database.dao.AdminDao;
import com.example.wando.daystarlaptops.database.dao.CheckInDao;
import com.example.wando.daystarlaptops.database.dao.ItemDao;
import com.example.wando.daystarlaptops.database.dao.LocationDao;
import com.example.wando.daystarlaptops.database.dao.OwnerDao;
import com.example.wando.daystarlaptops.database.entity.ActivityLog;
import com.example.wando.daystarlaptops.database.entity.Admin;
import com.example.wando.daystarlaptops.database.entity.CheckIn;
import com.example.wando.daystarlaptops.database.entity.Item;
import com.example.wando.daystarlaptops.database.entity.Location;
import com.example.wando.daystarlaptops.database.entity.Owner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Database(entities = {Admin.class, CheckIn.class, Item.class, Location.class, Owner.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract AdminDao adminDao();
    public abstract CheckInDao checkInDao();
    public abstract ItemDao itemDao();
    public abstract LocationDao locationDao();
    public abstract OwnerDao ownerDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context)
    {
        if(INSTANCE == null)
        {
            synchronized (AppDatabase.class)
            {
                if(INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_db").fallbackToDestructiveMigration().addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    // RoomDatabase callback for opening the db for repopulation
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    // AsyncTask class for opening and closing the db for repopulation on a background thread
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final LocationDao locationDao;
        private final OwnerDao ownerDao;
        private final ItemDao itemDao;
        private final AdminDao adminDao;
        private final CheckInDao checkInDao;

        PopulateDbAsync(AppDatabase db) {
            locationDao = db.locationDao();
            ownerDao = db.ownerDao();
            itemDao = db.itemDao();
            adminDao = db.adminDao();
            checkInDao = db.checkInDao();
        }


        // executed in background
        // adds the below checkIns to the db
        @Override
        protected Void doInBackground(final Void... params) {
            checkInDao.deleteAll();
            itemDao.deleteAll();
            adminDao.deleteAll();
            ownerDao.deleteAll();
            locationDao.deleteAll();

            SimpleDateFormat dateformat2 = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String strdate2 = "02-04-2013 11:35:42";
            Date newdate = null;
            try {
                newdate = dateformat2.parse(strdate2);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            // enter 5 records for Locations table
            Location location = new Location("1001", "Main Gate", "Athi River");
            locationDao.insert(location);
            location = new Location("1002", "Imani Hostel", "Athi River");
            locationDao.insert(location);
            location = new Location("1003", "Grace Hostel", "Athi River");
            locationDao.insert(location);
            location = new Location("1004", "Library", "Nairobi");
            locationDao.insert(location);
            location = new Location("1005", "Main Gate", "Nairobi");
            locationDao.insert(location);

            // enter 5 records for Admins table
            Admin admin = new Admin("1001","Julius Ceasar", "1002", "Security Guard", "password");
            adminDao.insert(admin);
            admin = new Admin("1002","Mark Antony", "1003", "Security Guard", "password");
            adminDao.insert(admin);
            admin = new Admin("1003","Augustus Ceasar", "1004", "System Admin", "password");
            adminDao.insert(admin);
            admin = new Admin("1004","Nero Claudius", "1005", "System Admin", "password");
            adminDao.insert(admin);
            admin = new Admin("1005","Marcus Aurelius", "1003", "Security Guard", "password");
            adminDao.insert(admin);
            admin = new Admin("1006","Monty Python", "1001", "Security Guard", "password");
            adminDao.insert(admin);

            // enter 5 records for Owners table
            Owner owner = new Owner("18-0030", "Beethoven", "1005");
            ownerDao.insert(owner);
            owner = new Owner("17-0030", "Mozart", "1004");
            ownerDao.insert(owner);
            owner = new Owner("16-0030", "Bach", "1003");
            ownerDao.insert(owner);
            owner = new Owner("15-0030", "Chopin", "1002");
            ownerDao.insert(owner);
            owner = new Owner("14-0030", "Handel", "1001");
            ownerDao.insert(owner);

            // enter 5 records for Items table
            Item item = new Item("1001", "Laptop", "Hp", "CND445GY14", "14-0030", newdate, null);
            itemDao.insert(item);
            item = new Item("1002", "Camera", "Canon", "EF70-20040LIS", "18-0030", newdate, null);
            itemDao.insert(item);
            item = new Item("1003", "Laptop", "Dell", "FGJ90K890", "15-0030", newdate, null);
            itemDao.insert(item);
            item = new Item("1004", "Laptop", "Lenovo", "GH890JIO9", "16-0030", newdate, null);
            itemDao.insert(item);
            item = new Item("1005", "Camera", "Kodak", "BZ39-20040LIS", "17-0030", newdate, null);
            itemDao.insert(item);
            item = new Item("1007", "Microphone", "Sony", "CG97-56740LIS", "14-0030", newdate, null);
            itemDao.insert(item);

            return null;
        }
    }
}
