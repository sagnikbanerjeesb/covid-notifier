package com.sagnik.covidnotifier.sync;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.services.CovidCountChangeNotificationService;

import javax.inject.Inject;

public class SyncWorker extends Worker {
    private NotificationManager mNotifyManager;

    @Inject
    CovidCountChangeNotificationService covidCountChangeNotificationService;

    public SyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        DaggerServiceDaggerComponent.builder().build().inject(this);
    }

    @Override
    public Result doWork() {
        this.covidCountChangeNotificationService.notifyCovidCountChanges(super.getApplicationContext());
        return Result.success();
    }
}