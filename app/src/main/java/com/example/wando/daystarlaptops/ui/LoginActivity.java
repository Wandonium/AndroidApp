package com.example.wando.daystarlaptops.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
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
import com.example.wando.daystarlaptops.R;
import com.example.wando.daystarlaptops.database.entity.Admin;
import com.example.wando.daystarlaptops.database.entity.Location;
import com.example.wando.daystarlaptops.networking.CheckUserLoginThread;
import com.example.wando.daystarlaptops.viewmodel.LoginViewModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button cmdLogin, cmdExit;
    private EditText userID, password;
    private String user_id, pssword, admin_id, admin_name, location_name, location_id, campus;
    private LoginViewModel viewModel;
    private Thread thread;
    private Handler handler;
    private Location location;
    private Admin admin;

    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize variables
        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        userID = findViewById(R.id.txtUsername);
        password = findViewById(R.id.txtPassword);
        cmdLogin=findViewById(R.id.cmdLogin);
        cmdExit = findViewById(R.id.cmdExit);
        AndroidNetworking.initialize(getApplicationContext());

        // set onClickListener for Login button
        cmdLogin.setOnClickListener(this);

        cmdExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAffinity(LoginActivity.this);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(userID.getText()) || TextUtils.isEmpty(password.getText())) {
            Toast.makeText(getApplicationContext(), "ERROR! Required field is empty.", Toast.LENGTH_SHORT).show();
        } else {
            user_id = userID.getText().toString();
            pssword = password.getText().toString();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Logging in. Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.show();
            // check admin details from server
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData();
                    String string = bundle.getString("DATA");
                    progressDialog.dismiss();
                    switch(string) {
                        case "UNSUCCESSFUL": {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("Login Error. Incorrect User ID or Password.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing.
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            break;
                        }
                        case "CONNECTION_ERROR": {
                            Toast.makeText(getApplicationContext(), "Login Error! No internet connection.", Toast.LENGTH_LONG).show();
                            break;
                        }
                        case "REQUEST_CANCELLED_ERROR": {
                            Toast.makeText(getApplicationContext(), "Login Error! Server error.", Toast.LENGTH_LONG).show();
                            break;
                        }
                        case "INDETERMINATE_ERROR": {
                            Toast.makeText(getApplicationContext(), "Login Error! Indeterminate error!", Toast.LENGTH_LONG).show();
                            break;
                        }
                        default: {
                            JsonParser parser = new JsonParser();
                            Object obj = parser.parse(string);
                            JsonArray array = (JsonArray)obj;
                            JsonObject jsonObject = (JsonObject)array.get(0);
                            admin_id = jsonObject.get("admin_id").toString();
                            admin_name = jsonObject.get("name").toString();
                            location_name = jsonObject.get("location_name").toString();
                            location_id = jsonObject.get("location_id").toString();
                            campus = jsonObject.get("campus").toString();

                            admin_id = admin_id.replaceAll("^\"|\"$", "");
                            admin_name = admin_name.replaceAll("^\"|\"$", "");
                            location_name = location_name.replaceAll("^\"|\"$", "");
                            location_id = location_id.replaceAll("^\"|\"$", "");
                            campus = campus.replaceAll("^\"|\"$", "");

                            location = new Location(location_id, location_name, campus);
                            admin = new Admin(admin_id, admin_name, location_id, "Guard", pssword);

                            List<Location> locations = viewModel.getAllLocations();
                            boolean addLocation = false;
                            for(Location theLocation: locations) {
                                if(location.getId().equals(theLocation.getId()))
                                    System.out.println(location.getId()+ " is already in the db.");
                                else
                                    addLocation = true;
                            }
                            if(addLocation)
                                viewModel.insertLocation(location);
                            List<Admin> admins = viewModel.getAllAdmins();
                            boolean addAdmin = false;
                            for(Admin theAdmin: admins) {
                                if(admin.getId().equals(theAdmin.getId()))
                                    System.out.println(admin.getId()+" is already in the db.");
                                else
                                    addAdmin = true;
                            }
                            if(addAdmin)
                                viewModel.insertAdmin(admin);
                            // go to next activity
                            Intent intent= new Intent(getApplicationContext(), ManualSignInActivity.class);
                            intent.putExtra("LOCATION_ID", location_id);
                            intent.putExtra("ADMIN_ID", admin_id);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                        }
                    }
                }
            };
            thread = new Thread(new CheckUserLoginThread(handler, user_id, pssword, getApplicationContext()));
            thread.start();

        }
    }
}
