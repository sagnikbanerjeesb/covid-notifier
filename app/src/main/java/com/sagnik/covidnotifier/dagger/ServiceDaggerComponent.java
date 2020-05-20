package com.sagnik.covidnotifier.dagger;

import com.sagnik.covidnotifier.loaders.DataLoader;
import com.sagnik.covidnotifier.sync.SyncAdapter;
import com.sagnik.covidnotifier.workers.SimpleWorker;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ExternalServicesModule.class, ConstantsModule.class})
public interface ServiceDaggerComponent {
    void inject(DataLoader dataLoader);
    void inject(SimpleWorker simpleWorker);
    void inject(SyncAdapter syncAdapter);
}
