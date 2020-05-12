package com.sagnik.covidnotifier.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.sagnik.covidnotifier.services.CovidDataService.COVID_HTTP_URL;
import static com.sagnik.covidnotifier.services.CovidDataService.STORAGE_FILE_NAME;

@Module
public class ConstantsModule {
    @Provides
    @Singleton
    @Named(COVID_HTTP_URL)
    public String covidHttpUrl() {
        return "https://api.covid19india.org/data.json";
    }

    @Provides @Singleton @Named(STORAGE_FILE_NAME)
    public String storageFileName() {
        return "data.json";
    }
}
