package com.sagnik.covidnotifier.sync;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.services.NotificationService;

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

        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
            Log.i(TAG, "Message priority: "+remoteMessage.getPriority());
            notificationService.notify(getApplicationContext(), "Push Notification", remoteMessage.getData().get("txt"), (int)(System.currentTimeMillis()/1000));
        }
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.i(TAG, "Refreshed token: " + token);
        FirebaseMessaging.getInstance().subscribeToTopic("demo_topic"); // todo should this be replicated on activity start ?
        Log.i(TAG, "Subscribed to topic");
    }
}
