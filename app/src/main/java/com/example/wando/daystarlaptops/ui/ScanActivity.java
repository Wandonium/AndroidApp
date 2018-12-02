package com.example.wando.daystarlaptops.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.wando.daystarlaptops.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView scannerView;
    private static final int REQUEST_CAMERA=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        scannerView= new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if (checkPermission()){
                Toast.makeText(this,"Camera permission granted",Toast.LENGTH_SHORT).show();
            }

            else{
                requestPermission();
            }


        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (checkPermission())
            {
                if (scannerView==null){
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                    scannerView.setResultHandler(this);
                    scannerView.startCamera();
                }
                else{
                    requestPermission();
                }
            }
        }

        else{
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        scannerView.stopCamera();
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(this,CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{CAMERA},REQUEST_CAMERA);
    }

    public void onRequestPermissionResult(int requestCode, String permission[],int grantResults[]){
        switch (requestCode)
        {
            case REQUEST_CAMERA:
                if (grantResults.length>0){
                    boolean cameraAccepted= grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                    }

                    else{
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(CAMERA)){
                                displayAlertMessage("Allow Access",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                                    requestPermissions(new String []{CAMERA},REQUEST_CAMERA);}
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }

                break;
        }
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener Listener){
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", Listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult=result.getText();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(ScanActivity.this, OwnerItemsActivity.class);
                intent.putExtra("OWNER_ID", scanResult);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Incorrect Scan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(ScanActivity.this);
            }
        });

        builder.setMessage(scanResult);
        AlertDialog alert=builder.create();
        alert.show();

    }
}
