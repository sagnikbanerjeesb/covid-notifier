package com.sagnik.covidnotifier.sync;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.services.NotificationService;
import com.sagnik.covidnotifier.services.registeredServices.DummyForegroundService;

import javax.inject.Inject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";

    @Inject
    NotificationService notificationService;

    public MyFirebaseMessagingService() {
        DaggerServiceDaggerComponent.builder().build().inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
            Log.i(TAG, "Message priority: "+remoteMessage.getPriority());
        }

        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationService.notify(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), (int)(System.currentTimeMillis()/1000));
        } else {
            Intent foregroundSvcIntent = new Intent(this, DummyForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(foregroundSvcIntent);
            } else {
                startService(foregroundSvcIntent);
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.i(TAG, "Refreshed token: " + token);
        FirebaseMessaging.getInstance().subscribeToTopic("demo_topic"); // todo should this be replicated on activity start ?
        Log.i(TAG, "Subscribed to topic");
    }
}
