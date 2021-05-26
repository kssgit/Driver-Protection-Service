package com.example.dps;

import android.app.Application;
import android.content.Context;

import com.example.dps.notification.NotificationHelper;

public class InitApplication extends Application {
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createNotificationChannel(getApplicationContext());
        NotificationHelper.refreshScheduledNotification(getApplicationContext());
    }
}
