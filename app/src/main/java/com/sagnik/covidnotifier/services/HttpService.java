package com.sagnik.covidnotifier.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HttpService {
    @Inject
    public HttpService() {}

    public String get(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (Exception e) {
            throw new RuntimeException("Invalid URL", e);
        }

        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String s = br.readLine();
            while (s != null) {
                sb.append(s);
                s = br.readLine();
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch and process data", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}
