package com.sagnik.covidnotifier.workers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sagnik.covidnotifier.R;
import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.dagger.ServiceDaggerComponent;
import com.sagnik.covidnotifier.models.Delta;
import com.sagnik.covidnotifier.services.CovidDataService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.content.Context.NOTIFICATION_SERVICE;

public class SimpleWorker extends Worker {
    @Inject
    CovidDataService covidDataService;

    private NotificationManager mNotifyManager;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public SimpleWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        ServiceDaggerComponent serviceDaggerComponent = DaggerServiceDaggerComponent.builder().build();
        serviceDaggerComponent.inject(this);

        mNotifyManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("channel_id",
                    "Covid Count Changed", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(VISIBILITY_PUBLIC);
            notificationChannel.setDescription("Notification from Covid Notifier about change in counts");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            List<Delta> deltaList = covidDataService.checkForUpdates(super.getApplicationContext());
            int count = 0;
            int notificationIdBase = (int) (System.currentTimeMillis() / 1000);
            for (Delta delta : deltaList) {
                StringBuilder notificationText = new StringBuilder();
                boolean prependPipe = false;
                if (delta.confirmed != 0) {
                    notificationText.append("Confirmed: " + delta.confirmed);
                    prependPipe = true;
                }
                if (delta.deaths != 0) {
                    if (prependPipe) notificationText.append(" | ");
                    notificationText.append("Deaths: " + delta.deaths);
                    prependPipe = true;
                }
                if (delta.recovered != 0) {
                    if (prependPipe) notificationText.append(" | ");
                    notificationText.append("Recovered: " + delta.recovered);
                }

                Notification notification = new NotificationCompat.Builder(super.getApplicationContext(), "channel_id")
                        .setContentTitle(delta.state + " Covid Count Changed")
                        .setContentText(notificationText.toString())
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .build();
                mNotifyManager.notify(notificationIdBase + count, notification);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to check for updates", e);
        }
        WorkManager.getInstance(super.getApplicationContext()).enqueue(new OneTimeWorkRequest.Builder(SimpleWorker.class)
                .addTag("covid-worker-task")
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build());
        return Result.success();
    }
}
