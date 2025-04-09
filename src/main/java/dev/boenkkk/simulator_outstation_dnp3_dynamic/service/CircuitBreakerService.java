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
        databaseService.addDoubleBitBinaryInput(ENDPOINT, circuitBreakerModel.getIndex()); // cb value
        databaseService.addBinaryOutput(ENDPOINT, circuitBreakerModel.getIndex()); // cb command
        databaseService.addBinaryInput(ENDPOINT, circuitBreakerModel.getIndex()); // cb local remote

        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<CircuitBreakerModel> circuitBreakerModels = Optional
                .ofNullable(outstationData.getCircuitBreakerModel()).orElseGet(ArrayList::new);
            circuitBreakerModels.add(circuitBreakerModel);

            outstationData.setCircuitBreakerModel(circuitBreakerModels);
        });
    }

    public CircuitBreakerModel getData(Integer index) {
        return outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getCircuitBreakerModel)
            .orElse(Collections.emptyList())
            .stream()
            .filter(model -> index.equals(model.getIndex()))
            .findFirst()
            // .orElseThrow(() -> new NotFoundException("Circuit breaker data not found for
            // name: " + name))
            .orElse(null);
    }

    public List<CircuitBreakerModel> getAll() {
        return outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getCircuitBreakerModel)
            .orElse(Collections.emptyList())
            .stream()
            .toList();
    }

    public void deleteData(Integer index) {
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<CircuitBreakerModel> circuitBreakerModels = outstationData.getCircuitBreakerModel();

            if (circuitBreakerModels != null) {
                // Remove all models matching the name (assuming CircuitBreakerModel has
                circuitBreakerModels.removeIf(model -> index.equals(model.getIndex()));

                databaseService.deleteDoubleBitBinaryInput(ENDPOINT, index); // cb value
                databaseService.deleteBinaryOutput(ENDPOINT, index); // cb command
                databaseService.deleteBinaryInput(ENDPOINT, index); // cb local remote
            }
        });
    }
}
