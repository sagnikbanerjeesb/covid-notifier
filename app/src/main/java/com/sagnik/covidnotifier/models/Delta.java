package com.sagnik.covidnotifier.models;

public class Delta {
    public long confirmed;
    public long deaths;
    public long recovered;
    public String state;

    public Delta() {
    }

    public Delta(long confirmed, long deaths, long recovered, String state) {
        this.confirmed = confirmed;
        this.deaths = deaths;
        this.recovered = recovered;
        this.state = state;
    }
}
