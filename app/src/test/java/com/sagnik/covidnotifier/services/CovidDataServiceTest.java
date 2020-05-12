package com.sagnik.covidnotifier.services;

import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponentTest;
import com.sagnik.covidnotifier.dagger.ServiceDaggerComponentTest;
import com.sagnik.covidnotifier.models.CovidData;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CovidDataServiceTest {
    @Inject
    CovidDataService covidDataService;

    @Before
    public void setUp() throws Exception {
        ServiceDaggerComponentTest serviceDaggerComponentTest = DaggerServiceDaggerComponentTest.builder().build();
        serviceDaggerComponentTest.inject(this);
    }

    @Test
    public void testDataService() throws Exception {
        Map<String, CovidData.Statewise> data = covidDataService.fetchCovidData();
        assertNotNull(data);
        assertTrue(data.size() > 1);
        System.out.println(data);
        System.out.println(data.size());
    }
}