package com.sagnik.covidnotifier.androidservices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.sagnik.covidnotifier.MainActivity;
import com.sagnik.covidnotifier.R;
import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.dagger.ServiceDaggerComponent;
import com.sagnik.covidnotifier.models.Delta;
import com.sagnik.covidnotifier.services.CovidDataService;
import com.sagnik.covidnotifier.utils.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import static android.app.Notification.VISIBILITY_PUBLIC;

public class CovidDataAndroidService extends Service {
    @Inject
    CovidDataService covidDataService;

    private Timer timer;

    private NotificationManager mNotifyManager;

    @Override
    public void onDestroy() {
        super.onDestroy();
        logger.info("Service destroyed!");
        stopTimer();
    }

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void onCreate() {
        super.onCreate();

        ServiceDaggerComponent serviceDaggerComponent = DaggerServiceDaggerComponent.builder().build();
        serviceDaggerComponent.inject(this);

        mNotifyManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
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

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "channel_id")
                .setContentTitle("Continuously checking for Covid Count Updates")
                .setSmallIcon(R.mipmap.covid_launcher_round)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        startTimer();

        logger.info("Service created!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTimer() {
        //set a new Timer
        timer = new Timer();

        Context context = getApplicationContext();

        //schedule the timer, to wake up every 1 second
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Running!");
                try {
                    List<Delta> deltaList = covidDataService.checkForUpdates(context);
                    int count = 0;
                    int notificationIdBase = (int) (System.currentTimeMillis() / 1000);
                    for (Delta delta : deltaList) {
                        StringBuilder notificationText = new StringBuilder();
                        boolean prependPipe = false;
                        if (delta.confirmed != 0) {
                            notificationText.append("Confirmed: " + Utils.formatNumber(delta.confirmed, true));
                            prependPipe = true;
                        }
                        if (delta.deaths != 0) {
                            if (prependPipe) notificationText.append(" | ");
                            notificationText.append("Deaths: " + Utils.formatNumber(delta.deaths, true));
                            prependPipe = true;
                        }
                        if (delta.recovered != 0) {
                            if (prependPipe) notificationText.append(" | ");
                            notificationText.append("Recovered: " + Utils.formatNumber(delta.recovered, true));
                        }

                        // Create an explicit intent for an Activity in your app
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                        Notification notification = new NotificationCompat.Builder(context, "channel_id")
                                .setContentTitle(delta.state + " Covid Count Changed")
                                .setContentText(notificationText.toString())
                                .setSmallIcon(R.mipmap.covid_launcher_round)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .build();
                        mNotifyManager.notify(notificationIdBase + count, notification);
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Failed to check for updates", e);
                }
            }
        }, 100, 300000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
