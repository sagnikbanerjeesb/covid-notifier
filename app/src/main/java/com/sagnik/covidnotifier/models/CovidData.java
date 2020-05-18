package com.sagnik.covidnotifier.models;

import java.util.List;

public class CovidData {
    public List<Statewise> statewise;

    @Override
    public String toString() {
        return "CovidData{" +
                "statewise=" + statewise +
                '}';
    }

    public static class Statewise {
        public long active;
        public long confirmed;
        public long deaths;
        public long deltaconfirmed;
        public long deltadeaths;
        public long deltarecovered;
        public String lastupdatedtime;
        public long recovered;
        public String state;
        public String statecode;
//        public String statenotes;

        @Override
        public String toString() {
            return "Statewise{" +
                    "active='" + active + '\'' +
                    ", confirmed='" + confirmed + '\'' +
                    ", deaths='" + deaths + '\'' +
                    ", deltaconfirmed='" + deltaconfirmed + '\'' +
                    ", deltadeaths='" + deltadeaths + '\'' +
                    ", deltarecovered='" + deltarecovered + '\'' +
                    ", lastupdatedtime='" + lastupdatedtime + '\'' +
                    ", recovered='" + recovered + '\'' +
                    ", state='" + state + '\'' +
                    ", statecode='" + statecode + '\'' +
//                    ", statenotes='" + statenotes + '\'' +
                    '}';
        }
    }
}
