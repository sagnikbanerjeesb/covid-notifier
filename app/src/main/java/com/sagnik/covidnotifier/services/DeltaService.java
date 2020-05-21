package com.sagnik.covidnotifier.services;

import com.sagnik.covidnotifier.models.CovidData;
import com.sagnik.covidnotifier.models.Delta;
import com.sagnik.covidnotifier.utils.Consts;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeltaService {

    @Inject
    public DeltaService() {}

    public List<Delta> getDelta(Map<String, CovidData.Statewise> oldData, Map<String, CovidData.Statewise> newData) {
        return newData.values().parallelStream().filter(statewise -> !statewise.state.equals(Consts.TOTAL_KEY))
                .map(newStatewise -> delta(oldData.get(newStatewise.state), newStatewise))
                .filter(deltaOptional -> deltaOptional.isPresent())
                .map(deltaOptional -> deltaOptional.get())
                .collect(Collectors.toList());
    }

    private Optional<Delta> delta(CovidData.Statewise oldData, CovidData.Statewise newData) {
        if (newData == null) return Optional.empty();
        if (oldData == null) {
            Delta delta = new Delta(newData.confirmed, newData.deaths, newData.recovered, newData.state);
            return Optional.of(delta);
        }

        if (newData.lastupdatedtime.equals(oldData.lastupdatedtime)) {
            return Optional.empty();
        }

        Delta delta = new Delta();
        delta.state = newData.state;
        delta.confirmed = newData.confirmed - oldData.confirmed;
        delta.deaths = newData.deaths - oldData.deaths;
        delta.recovered = newData.recovered - oldData.recovered;

        if (delta.confirmed != 0 || delta.deaths != 0 || delta.recovered != 0) return Optional.of(delta);
        else return Optional.empty();
    }
}
