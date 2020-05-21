package com.sagnik.covidnotifier.sync;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncActivator {
    public static final String SYNC_UNIQUE_NAME = "covidnotifier-sync";
    public static final int SYNC_INTERVAL = 15;

    @Inject
    public SyncActivator() {}

    public void activate(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                .Builder(SyncWorker.class, SYNC_INTERVAL, TimeUnit.MINUTES)
                .setInitialDelay(SYNC_INTERVAL, TimeUnit.MINUTES)
                .setConstraints(constraints).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(SYNC_UNIQUE_NAME,
                ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }
}
