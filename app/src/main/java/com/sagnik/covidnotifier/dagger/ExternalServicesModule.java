package com.sagnik.covidnotifier.dagger;

import com.sagnik.covidnotifier.services.HttpService;
import com.sagnik.covidnotifier.services.StorageService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ExternalServicesModule {
    @Provides
    @Singleton
    public HttpService httpService() {
        return new HttpService();
    }

    @Provides
    @Singleton
    public StorageService storageService() {
        return new StorageService();
    }
}
