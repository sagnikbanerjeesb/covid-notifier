package com.sagnik.covidnotifier;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.sagnik.covidnotifier.androidservices.CovidDataAndroidService;
import com.sagnik.covidnotifier.loaders.DataLoader;
import com.sagnik.covidnotifier.models.CovidData;
import com.sagnik.covidnotifier.workers.SimpleWorker;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Map<String, CovidData.Statewise>> {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private static final int LOAD_DATA = 0;

    private LinearLayout scrollViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollViewLayout = findViewById(R.id.scroll_view_layout);

        try {
            WorkManager.getInstance(this).cancelAllWorkByTag("covid-worker-task").getResult().get();
        } catch (ExecutionException | InterruptedException e) {
            logger.log(Level.SEVERE, "Exception while canelling worker task", e);
        }

        LoaderManager.getInstance(this).restartLoader(LOAD_DATA, new Bundle(), this);

//        WorkManager.getInstance(this).enqueue(new OneTimeWorkRequest.Builder(SimpleWorker.class)
//                .addTag("covid-worker-task")
//                .setInitialDelay(5, TimeUnit.SECONDS)
//                .build());

        if (!isMyServiceRunning(CovidDataAndroidService.class)) {
            startForegroundService(new Intent(getApplicationContext(), CovidDataAndroidService.class));
        }

    }

    @NonNull
    @Override
    public Loader<Map<String, CovidData.Statewise>> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == LOAD_DATA) {
            return new DataLoader(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, CovidData.Statewise>> loader, Map<String, CovidData.Statewise> data) {
        scrollViewLayout.removeAllViews();
        if (data == null || data.size() == 0) {
            addTextToScrollViewLayout("Data unavailable");
            return;
        }
        CovidData.Statewise total = data.get("Total");
        addTextToScrollViewLayout("Total Confirmed: " + total.confirmed);
        addTextToScrollViewLayout("Total Deaths: " + total.deaths);
        addTextToScrollViewLayout("Total Recovered: " + total.recovered);
        CovidData.Statewise tn = data.get("Tamil Nadu");
        addTextToScrollViewLayout("TN Confirmed: " + tn.confirmed);
        addTextToScrollViewLayout("TN Deaths: " + tn.deaths);
        addTextToScrollViewLayout("TN Recovered: " + tn.recovered);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, CovidData.Statewise>> loader) {

    }

    private void addTextToScrollViewLayout(String msg) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(25, 0, 25, 25);
        tv.setLayoutParams(lp);
        tv.setText(msg);

        scrollViewLayout.addView(tv);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
