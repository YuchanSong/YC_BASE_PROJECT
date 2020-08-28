package com.uchan.base.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.uchan.base.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.uchan.base.app.Init.context;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private PowerManager pm = null;
    private PowerManager.WakeLock wl = null;

    private static String mChannelId = "default";
    private static String mChannelName = "default";
    private static String mChannelDescription = "channel_description";

    public static int mLightColor = Color.GREEN;
    public static long[] mVibrationPattern = new long[]{100, 200, 100, 200};

    public static String getChannelId() {
        return mChannelId;
    }

    public static String getChannelName() {
        return mChannelName;
    }

    public static String getChannelDescription() {
        return mChannelDescription;
    }

    public static int getLightColor() {
        return mLightColor;
    }

    public static long[] getVibrationPattern() {
        return mVibrationPattern;
    }

    /**
     * WakeRock 해제 기능을 수행할 Runnable 객체
     */
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (wl != null) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wl.release();
            }
        }
    };

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.e("ycsong", "token = " + token);
    }

    // 메시지 수신
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }

    @SuppressLint("InvalidWakeLockTag")
    private void sendNotification(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        if (from != null && from.equals("google.com/iid")) {
            return;
        }

        String title = "";
        String body = "";
        RemoteMessage.Notification remoteMessageNotification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();

        if (remoteMessageNotification != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        } else {
            title = getString(data, "title", "알림");
            body = getString(data, "message", "");
        }

        String count = getString(data, "badge", "0");

        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wl = pm.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | PowerManager.ACQUIRE_CAUSES_WAKEUP, "FCMWakeLock");
        }
        wl.acquire();

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;

        int notiID = (int) System.currentTimeMillis();
        Intent in = new Intent(Intent.ACTION_MAIN);

        int badgeCount;
        try {
            badgeCount = Integer.parseInt(count);
        } catch (Exception e) {
            badgeCount = 0;
        }

        notification = normalStyle(context, title, body, notiID, in, badgeCount);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        } else {
            long[] vibrate = {1000, 1000, 1000, 1000, 1000};
            notification.vibrate = vibrate;
        }
        nm.notify(003123, notification);
        run.run();
    }

    /**
     * 전달 받은 데이터를 가공하여 normal 스타일의 notification 을 띄우는 메소드.<br>
     *
     * @param title   notification 제목 문자열.
     * @param message notification 내용 문자열.
     * @param notiID  notification 고유 Id 값.
     * @param in      이동할 화면 정보를 갖고 있는 intent 객체.
     * @return 생성된  normal 스타일의 Notification 객체.
     */
    private Notification normalStyle(Context context, String title, String message, int notiID, Intent in, int number) {
        if (title == null || title.equals("")) {
            title = context.getResources().getString(R.string.app_name);
        }

        int notiIconId = R.drawable.noti_icon;
        Notification notification;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notiID, in, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, getChannelId())
                .setSmallIcon(notiIconId)
                .setTicker("새 메시지 도착")
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setNumber(number);

        notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL; //선택시 자동삭제

        return notification;
    }

    private String getString(Map<String, String> data, String key, String defaultValue) {
        String result = data.get(key);

        if (TextUtils.isEmpty(result)) {
            result = defaultValue;
        }

        return result;
    }
}
