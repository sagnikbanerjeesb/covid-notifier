package com.sagnik.covidnotifier.services;

import android.content.Context;

import com.sagnik.covidnotifier.models.Delta;
import com.sagnik.covidnotifier.utils.Utils;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CovidCountChangeNotificationService {
    private NotificationService notificationService;
    private CovidDataService covidDataService;

    @Inject
    public CovidCountChangeNotificationService(NotificationService notificationService, CovidDataService covidDataService) {
        this.notificationService = notificationService;
        this.covidDataService = covidDataService;
    }

    public void notifyCovidCountChanges(Context context) {
        try {
            List<Delta> deltaList = covidDataService.checkForUpdates(context);
            if (deltaList.size() == 0) {
                this.notificationService.notify(context, "Covid count checked", "No changes detected", 5); // todo remove
                return;
            }
            int count = 0;
            int notificationIdBase = (int) (System.currentTimeMillis() / 1000);
            for (Delta delta : deltaList) {
                StringBuilder notificationText = new StringBuilder();
                boolean prependPipe = false;
                if (delta.confirmed != 0) {
                    notificationText.append("Confirmed: " + Utils.formatNumber(delta.confirmed, true));
                    prependPipe = true;
                }
                if (delta.deaths != 0) {
                    if (prependPipe) notificationText.append(" | ");
                    notificationText.append("Deaths: " + Utils.formatNumber(delta.deaths, true));
                    prependPipe = true;
                }
                if (delta.recovered != 0) {
                    if (prependPipe) notificationText.append(" | ");
                    notificationText.append("Recovered: " + Utils.formatNumber(delta.recovered, true));
                }

                this.notificationService.notify(context, delta.state + " covid count changed",
                        notificationText.toString(), notificationIdBase + count);
            }
        } catch (CovidDataService.OldDataAbsentException e) {
            // nothing needs to be done
        } catch (Exception e) {
            this.notificationService.notify(context, "Covid count check failure", e.getMessage(), 5); // todo remove
        }
    }
}
