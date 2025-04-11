package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.CircuitBreakerModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CircuitBreakerService {

    private static final String ENDPOINT = "0.0.0.0";
    private static final String PREFIX_NAME = "CB_";

    private final DatabaseService databaseService;
    private final OutstationsService outstationsService;

    public CircuitBreakerService(DatabaseService databaseService, OutstationsService outstationsService) {
        this.databaseService = databaseService;
        this.outstationsService = outstationsService;
    }

    public void addData(CircuitBreakerModel circuitBreakerModel) {
        databaseService.addDoubleBitBinaryInput(ENDPOINT, circuitBreakerModel.getIndexDbbiValue()); // cb value
        databaseService.addBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandOpenClose()); // command open/close
        databaseService.addBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandInvalid()); // command invalid
        databaseService.addBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandLocalRemote()); // command local/remote

        // Store model in outstation data
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<Object> dataPoints = Optional
                .ofNullable(outstationData.getListDataPoints())
                .orElseGet(ArrayList::new);
            String cbNewName = PREFIX_NAME + circuitBreakerModel.getName();
            circuitBreakerModel.setName(cbNewName);
            dataPoints.add(circuitBreakerModel);

            outstationData.setListDataPoints(dataPoints);
        });
    }

    public CircuitBreakerModel getData(String name) {
        return outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getListDataPoints)
            .orElse(Collections.emptyList())
            .stream()
            .filter(CircuitBreakerModel.class::isInstance)
            .map(CircuitBreakerModel.class::cast)
            .filter(model -> model.getName().equals(PREFIX_NAME + name))
            .findFirst()
            .orElse(null);
    }

    public List<CircuitBreakerModel> getAll() {
        return outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getListDataPoints)
            .orElse(Collections.emptyList())
            .stream()
            .filter(CircuitBreakerModel.class::isInstance)
            .map(CircuitBreakerModel.class::cast)
            .toList();
    }

    public void deleteData(CircuitBreakerModel circuitBreakerModel) {
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<Object> dataPoints = outstationData.getListDataPoints();

            if (dataPoints != null) {
                // Find the matching CircuitBreakerModel in the actual list
                Optional<CircuitBreakerModel> matchedModelOpt = dataPoints.stream()
                    .filter(CircuitBreakerModel.class::isInstance)
                    .map(CircuitBreakerModel.class::cast)
                    .filter(model -> model.getName().equals(PREFIX_NAME + circuitBreakerModel.getName()))
                    .findFirst();

                matchedModelOpt.ifPresent(matchedModel -> {
                    // Remove from original list (important!)
                    dataPoints.remove(matchedModel);
                    log.info("Removed model: {}", matchedModel);

                    // Remove from DNP3 database
                    databaseService.deleteDoubleBitBinaryInput(ENDPOINT, matchedModel.getIndexDbbiValue());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandOpenClose());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandInvalid());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandLocalRemote());
                });
            }
        });
    }
}
