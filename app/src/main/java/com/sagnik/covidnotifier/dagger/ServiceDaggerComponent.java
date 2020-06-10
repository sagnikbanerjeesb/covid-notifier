package com.sagnik.covidnotifier.dagger;

import com.sagnik.covidnotifier.MainActivity;
import com.sagnik.covidnotifier.loaders.DataLoader;
import com.sagnik.covidnotifier.services.registeredServices.DummyForegroundService;
import com.sagnik.covidnotifier.sync.MyAlarm;
import com.sagnik.covidnotifier.sync.SyncWorker;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ConstantsModule.class})
public interface ServiceDaggerComponent {
    void inject(DataLoader dataLoader);
    void inject(SyncWorker syncWorker);
    void inject(MainActivity mainActivity);
    void inject(MyAlarm myAlarm);

    void inject(DummyForegroundService dummyForegroundService);
}
