package com.hasanur.realtimehar.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hasanur.realtimehar.MainActivity;
import com.hasanur.realtimehar.R;

public class KeepAliveService extends Service {
    private static final String TAG = "KeepAliveService";
    private static final int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, getNotification());
        return START_STICKY;
    }

    private Notification getNotification() {
        // Create a PendingIntent to open the MainActivity when the notification is clicked
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create a notification channel for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "KeepAliveChannel",
                    "KeepAliveChannel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "KeepAliveChannel")
                .setContentTitle("Real Time HAR running")
                .setContentText("Sensor data collection on going ")
                .setSmallIcon(R.drawable.ic_download)
                .setContentIntent(pendingIntent)  // Set the PendingIntent
                .setPriority(NotificationCompat.PRIORITY_LOW);  // Set low priority to avoid disrupting the user

        return builder.build();
    }


}
