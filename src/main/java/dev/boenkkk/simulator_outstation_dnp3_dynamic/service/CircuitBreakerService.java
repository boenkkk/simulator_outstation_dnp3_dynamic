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

    private final DatabaseService databaseService;
    private final OutstationsService outstationsService;

    public CircuitBreakerService(DatabaseService databaseService, OutstationsService outstationsService) {
        this.databaseService = databaseService;
        this.outstationsService = outstationsService;
    }

    public void addData(CircuitBreakerModel circuitBreakerModel) {
        databaseService.addDoubleBitBinaryInput(ENDPOINT, circuitBreakerModel.getIndexValue()); // cb value
        databaseService.addBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexCommandOpenClose()); // command open/close
        databaseService.addBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexCommandInvalid()); // command invalid
        databaseService.addBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexCommandLocalRemote()); // command local/remote

        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<CircuitBreakerModel> circuitBreakerModels = Optional
                .ofNullable(outstationData.getCircuitBreakerModel()).orElseGet(ArrayList::new);
            circuitBreakerModels.add(circuitBreakerModel);

            outstationData.setCircuitBreakerModel(circuitBreakerModels);
        });
    }

    public CircuitBreakerModel getData(String name) {
        return outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getCircuitBreakerModel)
            .orElse(Collections.emptyList())
            .stream()
            .filter(model -> name.equals(model.getName()))
            .findFirst()
            .orElse(null);
    }

    public List<CircuitBreakerModel> getAll() {
        return outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getCircuitBreakerModel)
            .orElse(Collections.emptyList())
            .stream()
            .toList();
    }

    public void deleteData(CircuitBreakerModel circuitBreakerModel) {
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<CircuitBreakerModel> circuitBreakerModels = outstationData.getCircuitBreakerModel();

            if (circuitBreakerModels != null) {
                // Find the model that matches by name
                Optional<CircuitBreakerModel> matchedModelOpt = circuitBreakerModels.stream()
                    .filter(model -> circuitBreakerModel.getName().equals(model.getName()))
                    .findFirst();

                matchedModelOpt.ifPresent(matchedModel -> {
                    // remove bean
                    circuitBreakerModels.remove(matchedModel);

                    // remove dnp3 database
                    databaseService.deleteDoubleBitBinaryInput(ENDPOINT, matchedModel.getIndexValue()); // cb value
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexCommandOpenClose()); // command open/close
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexCommandInvalid()); // command invalid
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexCommandLocalRemote()); // command local/remote
                });
            }
        });
    }
}
