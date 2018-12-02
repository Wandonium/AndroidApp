package com.example.wando.daystarlaptops.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.example.wando.daystarlaptops.R;
import com.example.wando.daystarlaptops.database.DatabaseRepository;
import com.example.wando.daystarlaptops.database.entity.Item;
import com.example.wando.daystarlaptops.database.entity.Owner;
import com.example.wando.daystarlaptops.networking.ApiEndPoint;
import com.example.wando.daystarlaptops.networking.GetUserAndItemsThread;
import com.example.wando.daystarlaptops.viewmodel.ManualSignInViewModel;
import com.example.wando.daystarlaptops.viewmodel.OwnerItemsViewModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;

import okhttp3.Response;

public class ManualSignInActivity extends AppCompatActivity {

    private Button cmdCheck, cmdScan, cmdRefresh;
    private EditText et_owner_id;

    private ManualSignInViewModel viewModel;
    private String owner_id, owner_name, item_id, item_type, item_make, serial_no, reg_d, reg_dt, location_id;
    private List<Owner> owners;
    private List<Item> items;
    private Thread thread;
    private Handler handler;
    private ProgressDialog progressDialog;

    private static final String TAG = ManualSignInActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_sign_in);

        // get location ID from previous activity
        location_id = getIntent().getStringExtra("LOCATION_ID");

        owners = new ArrayList<>();
        items = new ArrayList<>();

        // initialize view model
        viewModel = ViewModelProviders.of(this).get(ManualSignInViewModel.class);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                final ProgressDialog progressDialog = new ProgressDialog(ManualSignInActivity.this);
                progressDialog.setMessage("Loading data, please wait...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.show();
                Bundle bundle = msg.getData();
                String string = bundle.getString("DATA");
                switch(string) {
                    case "UNSUCCESSFUL" :
                        errorMessage("AlertDialog", "Fatal Error. Failed to access data from server.");
                        break;
                    case "CONNECTION_ERROR" :
                        errorMessage("Toast", "Connection Error! No internet connection!");
                        break;
                    case "REQUEST_CANCELLED_ERROR" :
                        errorMessage("Toast", "Data Retrieval Error! Server Error.");
                        break;
                    case "INDETERMINATE_ERROR" :
                        errorMessage("Toast", "Data Retrieval Error! Indeterminate Error.");
                        break;
                    default: {
                        List<List<Object>> objects = parseData(string, "get");
                        List<Object> ownerObjects = objects.get(0);
                        List<Owner> theOwners = new ArrayList<>();
                        for(Object object: ownerObjects) {
                            Owner convertedOwner = (Owner) object;
                            theOwners.add(convertedOwner);
                        }

                        List<Object> ItemObjects = objects.get(1);
                        List<Item> theItems = new ArrayList<>();
                        for(Object object: ItemObjects) {
                            Item convertedItem = (Item) object;
                            theItems.add(convertedItem);
                        }
                        checkNewData(theOwners, theItems);
                    }
                }
                progressDialog.dismiss();
            }
        };
        thread = new Thread(new GetUserAndItemsThread(handler, location_id));
        thread.start();

        et_owner_id = findViewById(R.id.txtStudentId);

        cmdCheck=findViewById(R.id.cmdCheck);
        cmdScan=findViewById(R.id.cmdScan);
        cmdRefresh=findViewById(R.id.cmdRefresh);


        final OwnerItemsViewModel ownerItemsViewModel = ViewModelProviders.of(this).get(OwnerItemsViewModel.class);

        cmdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_owner_id.getText())) {
                    Toast.makeText(getApplicationContext(), "ERROR! Student ID field is empty!",Toast.LENGTH_LONG).show();
                } else {
                    String theID = et_owner_id.getText().toString();
                    Owner owner = ownerItemsViewModel.getOwner(theID);
                    if(owner == null)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManualSignInActivity.this);
                        builder.setMessage("The Student ID was not found in the Database. Click the Refresh button to refresh the database or enter a new Student ID.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing.
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else {
                        Intent intent= new Intent(v.getContext(),OwnerItemsActivity.class);
                        intent.putExtra("OWNER_ID", theID);
                        String admin_id = getIntent().getStringExtra("ADMIN_ID");
                        intent.putExtra("ADMIN_ID", admin_id);
                        intent.putExtra("LOCATION_ID", location_id);
                        v.getContext().startActivity(intent);
                    }
                }


            }
        });

        cmdScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(v.getContext(),ScanActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        cmdRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Item> items = viewModel.getAllItems();
                Date latestRegDate = null;
                try {
                    latestRegDate = dateFormat1.parse("1970-01-01");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                for(Item item: items) {
                    Date regDate = item.getReg_datetime();
                    if(regDate.after(latestRegDate)) {
                        latestRegDate = regDate;
                    }
                }
                System.out.println("Latest Reg time: "+latestRegDate);
                refreshUserAndItems(latestRegDate);
            }
        });
    }

    private void errorMessage(String type, final String message) {
        if(type.equals("Toast")) {
            ManualSignInActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        } else if(type.equals("AlertDialog")) {
            ManualSignInActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManualSignInActivity.this);
                    builder.setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing.
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
    }

    private List<List<Object>> parseData(String data, String function) {
        List<List<Object>> ownersAndItemsList = new ArrayList<>();
        JsonParser parser = new JsonParser();
        Object obj = parser.parse(data);
        JsonArray array = (JsonArray)obj;
        List<Object> theOwners = new ArrayList<>();
        List<Object> theItems = new ArrayList<>();
        Date reg_datetime = null, reg_date = null;
        for(int i = 0; i < array.size(); i++) {
            JsonObject jsonObject = (JsonObject) array.get(i);

            String ownerId = jsonObject.get("student_id").toString();
            String ownerName = jsonObject.get("name").toString();
            String itemType = jsonObject.get("item").toString();
            String itemMake = jsonObject.get("make").toString();
            String serialNo = jsonObject.get("serial_number").toString();
            String itemId = jsonObject.get("item_id").toString();
            String regD = jsonObject.get("reg_date").toString();
            String regDt = "";
            if(function.equals("get"))
                regDt = jsonObject.get("reg_datetime").toString();

            ownerId = ownerId.replaceAll("^\"|\"$", "");
            ownerName = ownerName.replaceAll("^\"|\"$", "");
            itemType = itemType.replaceAll("^\"|\"$", "");
            itemMake = itemMake.replaceAll("^\"|\"$", "");
            serialNo = serialNo.replaceAll("^\"|\"$", "");
            itemId = itemId.replaceAll("^\"|\"$", "");
            regD = regD.replaceAll("^\"|\"$", "");
            regDt = regDt.replaceAll("^\"|\"$", "");
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

            try {
                if(function.equals("get"))
                    reg_datetime = dateFormat.parse(regDt);
                reg_date = dateFormat1.parse(regD);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            theOwners.add(new Owner(ownerId, ownerName, location_id));
            if(function.equals("get"))
                theItems.add(new Item(itemId, itemType, itemMake, serialNo, ownerId, reg_datetime, reg_date));
            else
                theItems.add(new Item(itemId, itemType, itemMake, serialNo, ownerId, null, reg_date));
        }
        ownersAndItemsList.add(theOwners);
        ownersAndItemsList.add(theItems);
        return ownersAndItemsList;
    }

    private void checkNewData(List<Owner> theOwners, List<Item> theItems) {
        List<Owner> owners = viewModel.getAllOwners();
        List<Owner> newOwners = new ArrayList<>();
        List<Item> items = viewModel.getAllItems();
        List<Item> newItems = new ArrayList<>();
        for(Owner theOwner: theOwners) {  // from network
            Owner o = null;
            boolean add = true;
            for(Owner owner: owners) {    // from db
                o = theOwner;
                if(owner.getId().equals(theOwner.getId())) {
                    add = false;
                }
            }
            if(add)
                newOwners.add(o);
            else
                o = null;
        }
        for(Item theItem: theItems) {  // from network
            Item i = null;
            boolean add = true;
            for(Item item: items) {    // from db
                i = theItem;
                if(item.getId().equals(theItem.getId())) {
                    add = false;
                }
            }
            if(add)
                newItems.add(i);
            else
                i = null;
        }
        if(newOwners.size() == 0 || newItems.size() == 0) {
            errorMessage("Toast", "No new data from server.");
        } else {
            viewModel.insertOwners(newOwners);
            viewModel.insertItems(newItems);
            errorMessage("Toast", "New data added from server.");
        }
    }

    private void refreshUserAndItems(Date regDate) {
        final ProgressDialog progressDialog = new ProgressDialog(ManualSignInActivity.this);
        progressDialog.setMessage("Refreshing data, please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.show();
        AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.REFRESH_USER_AND_ITEMS)
                .setTag(this)
                .addBodyParameter("location_id", location_id)
                .addBodyParameter("datetime", regDate.toString())
                .setPriority(Priority.HIGH)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        // do anything with response
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.d(TAG, "Headers :" + response.headers());
                            String contentType = response.headers().get("Content-Type");
                            if(contentType.equals("application/json"))
                            {
                                String details = "";
                                try {
                                    details = response.body().source().readUtf8();
                                    Log.d(TAG, "response for refreshUserAndItems : " + details);
                                } catch(IOException e) { e.printStackTrace(); }
                                catch(NetworkOnMainThreadException e) { e.printStackTrace(); }

                                List<List<Object>> objects = parseData(details, "refresh");
                                List<Object> ownerObjects = objects.get(0);
                                List<Owner> theOwners = new ArrayList<>();
                                for(Object object: ownerObjects) {
                                    Owner convertedOwner = (Owner) object;
                                    theOwners.add(convertedOwner);
                                }

                                List<Object> ItemObjects = objects.get(1);
                                List<Item> theItems = new ArrayList<>();
                                for(Object object: ItemObjects) {
                                    Item convertedItem = (Item) object;
                                    theItems.add(convertedItem);
                                }
                                checkNewData(theOwners, theItems);

                            } else {
                                progressDialog.dismiss();
                                errorMessage("AlertDialog","Fatal Error. Failed to access data from server.");
                            }
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        // handle error
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                            if(error.getErrorDetail().equals("connectionError"))
                            {
                                errorMessage("Toast", "Login Error! No internet connection.");
                            }else if(error.getErrorDetail().equals("requestCancelledError"))
                                errorMessage("Toast", "Login Error! Server error.");
                            else
                                errorMessage("Toast", "Login Error! Indeterminate error!");
                        }
                    }
                });
    }
}
