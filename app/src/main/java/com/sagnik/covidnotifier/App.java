package com.sagnik.covidnotifier;

import android.app.Application;
import android.util.Log;

import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.dagger.ServiceDaggerComponent;

public class App extends Application {
    public ServiceDaggerComponent serviceDaggerComponent; // we can keep this private as well
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Application", "created");
        serviceDaggerComponent = DaggerServiceDaggerComponent.create();
    }
}
