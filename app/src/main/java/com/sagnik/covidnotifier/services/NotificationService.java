package com.sagnik.covidnotifier.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.sagnik.covidnotifier.MainActivity;
import com.sagnik.covidnotifier.R;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.content.Context.NOTIFICATION_SERVICE;

@Singleton
public class NotificationService {
    public static final String CHANNEL_ID = "covid_notifier_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Covid Count Changed";
    private NotificationManager notificationManager;

    @Inject
    public NotificationService() {}

    private void register(Context context) {
        if (notificationManager != null) return;

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME, NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(VISIBILITY_PUBLIC);
            notificationChannel.setDescription("Notification from Covid Notifier about change in counts");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public synchronized void notify(Context context, String title, String text, int id) {
        register(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.covid_launcher_round)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(id, notification);
    }

    public Notification buildNotificationForForegroundSvc(Context context, String title) {
        register(context);

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.covid_launcher_round)
                .build();
    }
}
