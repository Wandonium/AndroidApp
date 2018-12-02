package com.example.wando.daystarlaptops.networking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.example.wando.daystarlaptops.ui.ManualSignInActivity;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;

import okhttp3.Response;

public class GetUserAndItemsThread implements Runnable {

    private Handler handler;
    private String locationID;

    private static final String TAG = GetUserAndItemsThread.class.getName();

    public GetUserAndItemsThread(Handler handler, String locationID) {
        this.handler = handler;
        this.locationID = locationID;
    }

    @Override
    public void run() {
        final Message message = Message.obtain();
        final Bundle bundle = new Bundle();
        AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.GET_USER_AND_ITEMS)
                .setTag(this)
                .addBodyParameter("location_id", locationID)
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
                            Log.d(TAG, "Headers :" + response.headers());
                            String contentType = response.headers().get("Content-Type");
                            if(contentType.equals("application/json"))
                            {
                                String details = "";
                                try {
                                    details = response.body().source().readUtf8();
                                    Log.d(TAG, "response : " + details);
                                } catch(IOException e) { e.printStackTrace(); }
                                catch(NetworkOnMainThreadException e) { e.printStackTrace();}
                                bundle.putString("DATA", details);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            } else {
                                bundle.putString("DATA", "UNSUCCESSFUL");
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        }
                    }
                    @Override
                    public void onError(ANError error) {
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
                                bundle.putString("DATA", "CONNECTION_ERROR");
                            }else if(error.getErrorDetail().equals("requestCancelledError"))
                                bundle.putString("DATA", "REQUEST_CANCELLED_ERROR");
                            else
                                bundle.putString("DATA", "INDETERMINATE_ERROR");
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                });
    }
}
