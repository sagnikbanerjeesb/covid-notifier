package com.sagnik.covidnotifier.loaders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.sagnik.covidnotifier.App;
import com.sagnik.covidnotifier.models.CovidData;
import com.sagnik.covidnotifier.services.CovidCountChangeNotificationService;
import com.sagnik.covidnotifier.services.CovidDataService;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

public class DataLoader extends AsyncTaskLoader<Map<String, CovidData.Statewise>> {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    CovidDataService covidDataService;

    @Inject
    CovidCountChangeNotificationService covidCountChangeNotificationService;

    public DataLoader(@NonNull Context context) {
        super(context);
        ((App) context.getApplicationContext()).serviceDaggerComponent.inject(this);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public Map<String, CovidData.Statewise> loadInBackground() {
        try {
            covidCountChangeNotificationService.notifyCovidCountChanges(super.getContext());
            return covidDataService.fetchCovidData();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception while loading data in background", e);
            return null;
        }
    }
}
