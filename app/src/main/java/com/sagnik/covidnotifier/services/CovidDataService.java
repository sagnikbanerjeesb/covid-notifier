package com.sagnik.covidnotifier.services;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagnik.covidnotifier.models.CovidData;
import com.sagnik.covidnotifier.models.Delta;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class CovidDataService {
    public static final String COVID_HTTP_URL = "covidHttpUrl";
    public static final String STORAGE_FILE_NAME = "storageFileName";

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private HttpService httpService;
    private StorageService storageService;
    private DeltaService deltaService;
    private String covidHttpUrl;
    private String storageFileName;

    private Optional<Map<String, CovidData.Statewise>> cachedData = Optional.empty();

    @Inject
    public CovidDataService(HttpService httpService, StorageService storageService, DeltaService deltaService,
                            @Named(COVID_HTTP_URL) String covidHttpUrl, @Named(STORAGE_FILE_NAME) String storageFileName) {
        this.httpService = httpService;
        this.storageService = storageService;
        this.deltaService = deltaService;
        this.covidHttpUrl = covidHttpUrl;
        this.storageFileName = storageFileName;
    }

    public synchronized List<Delta> checkForUpdates(Context context) throws OldDataAbsentException {
        if (!cachedData.isPresent()) {
            cachedData = fetchOldData(context);
            if (!cachedData.isPresent()) {
                cachedData = Optional.of(fetchLatestData());
                storageService.writeToFile(context, storageFileName, new Gson().toJson(cachedData.get()));
                throw new OldDataAbsentException();
            }
        }
        Map<String, CovidData.Statewise> newData = fetchLatestData();
        List<Delta> deltaList = deltaService.getDelta(cachedData.get(), newData);
        if (deltaList.size() > 0) {
            cachedData = Optional.of(newData);
            storageService.writeToFile(context, storageFileName, new Gson().toJson(cachedData.get()));
        }
        return deltaList;
    }

    public synchronized Map<String, CovidData.Statewise> fetchCovidData() {
        return cachedData.orElseGet(() -> fetchLatestData());
    }

    private Map<String, CovidData.Statewise> fetchLatestData() {
        String dataStr = httpService.get(covidHttpUrl);
        CovidData covidData = new Gson().fromJson(dataStr, CovidData.class);
        Map<String, CovidData.Statewise> data = covidData.statewise.parallelStream()
                .collect(Collectors.toMap(statewise -> statewise.state, statewise -> statewise));
        return data;
    }

    private Optional<Map<String, CovidData.Statewise>> fetchOldData(Context context) {
        String dataStr = storageService.readFromFile(context, storageFileName);
        if (dataStr == null || dataStr.length() == 0) return Optional.empty();

        Map<String, CovidData.Statewise> data = new Gson().fromJson(dataStr, new TypeToken<Map<String, CovidData.Statewise>>() {}.getType());
        return Optional.of(data);
    }

    public static class OldDataAbsentException extends Exception {}
}
