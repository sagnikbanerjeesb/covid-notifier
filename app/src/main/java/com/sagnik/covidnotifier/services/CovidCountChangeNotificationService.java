package com.sagnik.covidnotifier.services;

import android.content.Context;
import android.util.Log;

import com.sagnik.covidnotifier.models.Delta;
import com.sagnik.covidnotifier.utils.Consts;
import com.sagnik.covidnotifier.utils.Utils;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CovidCountChangeNotificationService {
    public static final String NOTIFICATION_TITLE_SUFFIX = " covid count changed";
    public static final String COUNT_SEPERATOR = " | ";
    public static final int MILLIS_IN_ONE_SECOND = 1000;

    private NotificationService notificationService;
    private CovidDataService covidDataService;

    @Inject
    public CovidCountChangeNotificationService(NotificationService notificationService, CovidDataService covidDataService) {
        this.notificationService = notificationService;
        this.covidDataService = covidDataService;
    }

    public void notifyCovidCountChanges(Context context) {
        int notificationIdBase = (int) (System.currentTimeMillis() / MILLIS_IN_ONE_SECOND);
        try {
            List<Delta> deltaList = covidDataService.checkForUpdates(context);
            int count = 0;
            for (Delta delta : deltaList) {
                StringBuilder notificationText = new StringBuilder();
                boolean prependPipe = false;
                if (delta.confirmed != 0) {
                    notificationText.append(Consts.CONFIRMED_TXT + Utils.formatNumber(delta.confirmed, true));
                    prependPipe = true;
                }
                if (delta.deaths != 0) {
                    if (prependPipe) notificationText.append(COUNT_SEPERATOR);
                    notificationText.append(Consts.DECEASED_TXT + Utils.formatNumber(delta.deaths, true));
                    prependPipe = true;
                }
                if (delta.recovered != 0) {
                    if (prependPipe) notificationText.append(COUNT_SEPERATOR);
                    notificationText.append(Consts.RECOVERED_TXT + Utils.formatNumber(delta.recovered, true));
                }

                this.notificationService.notify(context, delta.state + NOTIFICATION_TITLE_SUFFIX,
                        notificationText.toString(), notificationIdBase + count++);
            }
        } catch (CovidDataService.OldDataAbsentException e) {
            // nothing needs to be done
        } catch (Exception e) {
            this.notificationService.notify(context, "Covid count check failure", e.getMessage(), notificationIdBase); // todo is there a better way?
        }
    }
}
