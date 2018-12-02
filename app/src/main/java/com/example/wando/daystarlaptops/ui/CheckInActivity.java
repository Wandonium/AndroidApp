package com.example.wando.daystarlaptops.ui;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.wando.daystarlaptops.R;
import com.example.wando.daystarlaptops.database.entity.CheckIn;
import com.example.wando.daystarlaptops.networking.ApiEndPoint;
import com.example.wando.daystarlaptops.viewmodel.CheckInViewModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CheckInActivity extends AppCompatActivity {

    private CheckInViewModel mCheckInViewModel;
    private String location_id;
    private List<CheckIn> toUpload;

    public static final int OWNER_ITEMS_ACTIVITY_REQUEST_CODE = 1;

    private static final String TAG = CheckInActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        mCheckInViewModel = ViewModelProviders.of(this).get(CheckInViewModel.class);

        location_id = getIntent().getStringExtra("LOCATION_ID");

        final String dummyTimeOut = "Waiting for check out!!";

        final String check_activity = getIntent().getStringExtra("CHECK_ACTIVITY");
        if(check_activity.equals("check_in"))
        {
            String[][] foreignKeys = null;
            Object[] objectArray = (Object[]) getIntent().getExtras().getSerializable("CHECKIN_TABLE_FOREIGN_KEYS");
            if(objectArray != null)
            {
                foreignKeys = new String[objectArray.length][];
                for(int i = 0; i < objectArray.length; i++)
                    foreignKeys[i] = (String[]) objectArray[i];
            }
            final String[] checkInIDs = mCheckInViewModel.getCheckInIDs();
            int maxId = 0;
            for(String e: checkInIDs)
            {
                int id = Integer.parseInt(e);
                if(id > maxId)
                    maxId = id;
            }
            String the_owner_id = foreignKeys[0][2];
            List<CheckIn> ownerCheckIns = mCheckInViewModel.getCheckInsForOwner(the_owner_id);
            boolean insertCheckIn = true;
            for(CheckIn checkIn: ownerCheckIns) {
                if (checkIn != null) {
                    if (checkIn.getTime_out().equals(dummyTimeOut)) {
                        insertCheckIn = false;
                        checkErrorDialog(checkIn);
                    }
                }
            }
            if(insertCheckIn) {
                for(int i = 0; i < foreignKeys.length; i++)
                {
                    Date checkDate = new java.sql.Date(new Date().getTime());
                    String admin_id = foreignKeys[i][0];
                    String item_id = foreignKeys[i][1];
                    String owner_id = foreignKeys[i][2];
                    maxId++;
                    CheckIn checkIn = new CheckIn(Integer.toString(maxId),convertTime(),dummyTimeOut,location_id, admin_id, item_id, owner_id,checkDate.toString());
                    mCheckInViewModel.insert(checkIn);
                }
            }
        }
        else if(check_activity.equals("check_out"))
        {
            String owner_id = getIntent().getStringExtra("OWNER_ID");
            List<CheckIn> ownerCheckIns = mCheckInViewModel.getCheckInsForOwner(owner_id);
            for(CheckIn checkIn: ownerCheckIns) {
                if(checkIn == null){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CheckInActivity.this);
                    builder.setMessage("Fatal Error! Trying to check out without first checkin in.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(CheckInActivity.this, ManualSignInActivity.class);
                                    intent.putExtra("LOCATION_ID", location_id);
                                    startActivity(intent);
                                    /*alert.dismiss();*/
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                } else if(checkIn.getTime_out().equals(dummyTimeOut)){
                    CheckIn check_out = new CheckIn(checkIn);
                    check_out.setTime_out(convertTime());
                    mCheckInViewModel.deleteCheckIn(checkIn);
                    mCheckInViewModel.insert(check_out);
                }
            }

        }

        Button logout = findViewById(R.id.btn_logout);

        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                List<CheckIn> checkIns = mCheckInViewModel.getAllCheckIns();
                toUpload = new ArrayList<>();
                boolean checkTimeout = true;
                for(CheckIn checkIn: checkIns) {
                    if(checkIn.getTime_out().equals(dummyTimeOut)) {
                        checkTimeout = false;
                    } else {
                        int id = Integer.parseInt(checkIn.getAdmin_id());
                        if(id == 28) {
                            toUpload.add(checkIn);
                        }
                    }
                }
                if(checkTimeout) {
                    String json = new Gson().toJson(toUpload);
                    System.out.println("JSON String: "+json);
                    senddata(json);
                } else {
                    errorMessage("AlertDialog","Can't logout without checking out all Check Ins.");
                }
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final CheckInAdapter adapter = new CheckInAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCheckInViewModel.getCheckIns().observe(this, new Observer<List<CheckIn>>() {
            @Override
            public void onChanged(@Nullable final List<CheckIn> checkIns) {
                // Update the cached copy of the words in the adapter.
                adapter.setCheckInList(checkIns);
            }
        });

    }

    public void senddata(String text) {
        File file = new File(getApplicationContext().getFilesDir(),"data.txt");
        if(!file.exists()){
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("data.txt", getApplicationContext().MODE_PRIVATE));
                outputStreamWriter.write(text);
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }


        final ProgressDialog progressDialog = new ProgressDialog(CheckInActivity.this);
        progressDialog.setMessage("Sending data to server, please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.show();

        AndroidNetworking.post("http://unbolted-math.000webhostapp.com/laptopregistrationsystem/mobile/updatecheckins.php")
                .addFileBody(new File(getFilesDir(), "data.txt")) // posting any type of file
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                       Log.d(TAG, "Response string: "+response);
                       String success = "checkins data has been successfully updated";
                       if(response.equals(success)) {
                           Intent intent = new Intent(CheckInActivity.this, LoginActivity.class);
                           startActivity(intent);
                           overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                       } else {
                           progressDialog.dismiss();
                           errorMessage("AlertDialog","Fatal Error. Failed to send data to server. Please consult your System Administrator!");
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
                                errorMessage("Toast", "Logout Error! No internet connection.");
                            }else if(error.getErrorDetail().equals("requestCancelledError"))
                                errorMessage("Toast", "Logout Error! Server error.");
                            else
                                errorMessage("Toast", "Logout Error! Indeterminate error!");
                        }
                    }
                });

    }

    public String convertTime() {
        Date date = new Date();
        int hrs = date.getHours();
        int minutes = date.getMinutes();
        int seconds = date.getSeconds();
        String time = "";
        String theHours = Integer.toString(hrs);
        String theMinutes = Integer.toString(minutes);
        String theSeconds = Integer.toString(seconds);
        if(hrs<10)
            theHours = "0" + theHours;
        if(minutes<10)
            theMinutes = "0" + theMinutes;
        if(seconds<10)
            theSeconds = "0" + theSeconds;
        time = theHours + ":" + theMinutes + ":" + theSeconds;
        return time;
    }

    private void checkErrorDialog(CheckIn checkIn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        final LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_checkout_error, null);

        // init views in layout
        final TextView owner_id, owner_name, item_type, time_in, time_out;
        owner_id = dialogView.findViewById(R.id.tv_id_of_owner);
        owner_name = dialogView.findViewById(R.id.tv_name_of_owner);
        item_type = dialogView.findViewById(R.id.tv_type_of_item);
        time_in = dialogView.findViewById(R.id.tv_check_in_time);
        time_out = dialogView.findViewById(R.id.tv_check_out_time);

        // set text for TextViews
        owner_id.setText(checkIn.getOwner_id());
        owner_name.setText(mCheckInViewModel.getOwnerName(checkIn));
        item_type.setText(mCheckInViewModel.getItemType(checkIn));
        time_in.setText(checkIn.getTime_in());
        time_out.setText(checkIn.getTime_out());

        // set up dialog builder
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(CheckInActivity.this, ManualSignInActivity.class);
                        intent.putExtra("LOCATION_ID", location_id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(CheckInActivity.this, ManualSignInActivity.class);
                        intent.putExtra("LOCATION_ID", location_id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
        AppCompatDialog dialog = builder.create();
        dialog.show();
    }

    private void errorMessage(String type, final String message) {
        if(type.equals("Toast")) {
            CheckInActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        } else if(type.equals("AlertDialog")) {
            CheckInActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CheckInActivity.this);
                    builder.setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing.
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
    }

}
