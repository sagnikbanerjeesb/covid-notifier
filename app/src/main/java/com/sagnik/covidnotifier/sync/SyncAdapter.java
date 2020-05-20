package com.sagnik.covidnotifier.sync;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.sagnik.covidnotifier.MainActivity;
import com.sagnik.covidnotifier.R;
import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.dagger.ServiceDaggerComponent;
import com.sagnik.covidnotifier.models.Delta;
import com.sagnik.covidnotifier.services.CovidDataService;
import com.sagnik.covidnotifier.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver contentResolver;

    @Inject
    CovidDataService covidDataService;

    private NotificationManager mNotifyManager;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        ServiceDaggerComponent serviceDaggerComponent = DaggerServiceDaggerComponent.builder().build();
        serviceDaggerComponent.inject(this);

        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        contentResolver = context.getContentResolver();

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

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        try {
            Context context = super.getContext();
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
            Log.i("SyncAdapter", "Synced");
        } catch (Exception e) {
            Log.e("SyncAdapter", "Failed to check for updates", e);
        }
    }
}