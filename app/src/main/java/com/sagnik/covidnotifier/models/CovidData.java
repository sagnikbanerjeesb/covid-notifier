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
        public String active;
        public String confirmed;
        public String deaths;
        public String deltaconfirmed;
        public String deltadeaths;
        public String deltarecovered;
        public String lastupdatedtime;
        public String recovered;
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
