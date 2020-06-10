package com.sagnik.covidnotifier.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.sagnik.covidnotifier.MainActivity;
import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.services.registeredServices.DummyForegroundService;

import static android.content.Context.ALARM_SERVICE;

public class MyAlarm extends BroadcastReceiver {
    public MyAlarm() {
        DaggerServiceDaggerComponent.builder().build().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Intent foregroundIntent = new Intent(context, DummyForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(foregroundIntent);
            } else {
                context.startService(foregroundIntent);
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, new Intent(context, MyAlarm.class), PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent pendingIntentActivity = PendingIntent.getActivity(context, 2, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + 5*60*1000, pendingIntentActivity), pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
}