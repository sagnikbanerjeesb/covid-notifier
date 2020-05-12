package com.sagnik.covidnotifier.loaders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.dagger.ServiceDaggerComponent;
import com.sagnik.covidnotifier.models.CovidData;
import com.sagnik.covidnotifier.services.CovidDataService;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

public class DataLoader extends AsyncTaskLoader<Map<String, CovidData.Statewise>> {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    CovidDataService covidDataService;

    public DataLoader(@NonNull Context context) {
        super(context);
        ServiceDaggerComponent serviceDaggerComponent = DaggerServiceDaggerComponent.builder().build();
        serviceDaggerComponent.inject(this);
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
            covidDataService.checkForUpdates(super.getContext());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception while checking for updates during load", e);
        }
        try {
            return covidDataService.fetchCovidData();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception while loading data in background", e);
            return null;
        }
    }
}
