package com.sagnik.covidnotifier.services.registeredServices;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.services.CovidCountChangeNotificationService;
import com.sagnik.covidnotifier.services.NotificationService;

import java.util.concurrent.Executors;

import javax.inject.Inject;

public class DummyForegroundService extends Service {
    private static final String TAG = "DummyForegroundService";

    @Inject
    CovidCountChangeNotificationService covidCountChangeNotificationService;

    @Inject
    NotificationService notificationService;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerServiceDaggerComponent.builder().build().inject(this);

        Notification foregroundNotification = notificationService.buildNotificationForForegroundSvc(this, "Checking for covid count updates");
        startForeground(1, foregroundNotification);

        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, "onStart ; startId: "+startId);

        if (intent != null && intent.getExtras() != null) {
            String d = intent.getExtras().getString("d", null);
            Log.i(TAG, "extras: "+d);
        }

        Executors.newSingleThreadExecutor().submit(() -> {
            covidCountChangeNotificationService.notifyCovidCountChanges(this);
            this.stopSelf();
        });

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
