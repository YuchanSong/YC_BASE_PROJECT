package com.uchan.base.app;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.uchan.base.receiver.NetworkChangeReceiver;

public class Init extends Application {

    private NetworkChangeReceiver networkChangeReceiver;
    private final String TAG = "ycsong";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "registerReceiver");
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    public void unregisterReceiver() {
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }
}
