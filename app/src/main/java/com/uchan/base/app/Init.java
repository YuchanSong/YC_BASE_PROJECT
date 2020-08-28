package com.uchan.base.app;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.os.Build;

import com.uchan.base.fcm.FirebaseMessagingService;
import com.uchan.base.receiver.NetworkChangeReceiver;

public class Init extends Application {

    private final String TAG = "ycsong";
    private NetworkChangeReceiver networkChangeReceiver;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        createNotificationChannel();

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    public void unregisterReceiver() {
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

    private void createNotificationChannel() {
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(FirebaseMessagingService.getChannelId(), FirebaseMessagingService.getChannelName(), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(FirebaseMessagingService.getChannelDescription());
            notificationChannel.setShowBadge(true);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(FirebaseMessagingService.getLightColor());
            notificationChannel.setVibrationPattern(FirebaseMessagingService.getVibrationPattern());
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(notificationChannel);
        }
    }

}
