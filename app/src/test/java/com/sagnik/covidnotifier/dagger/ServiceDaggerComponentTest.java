package com.sagnik.covidnotifier.dagger;

import com.sagnik.covidnotifier.services.CovidDataServiceTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ExternalServicesModule.class, ConstantsModule.class})
public interface ServiceDaggerComponentTest {
    void inject(CovidDataServiceTest covidDataServiceTest);
}