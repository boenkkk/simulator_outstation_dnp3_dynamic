package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.CircuitBreakerModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;
import lombok.extern.slf4j.Slf4j;
import org.joou.UShort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CircuitBreakerService {

    private static final String ENDPOINT = "0.0.0.0";
    private static final String PREFIX_NAME = "CB_";

    private final DatabaseService databaseService;
    private final OutstationsService outstationsService;
    private final DatapointService datapointService;

    public CircuitBreakerService(DatabaseService databaseService, OutstationsService outstationsService, DatapointService datapointService) {
        this.databaseService = databaseService;
        this.outstationsService = outstationsService;
        this.datapointService = datapointService;
    }

    public void addData(CircuitBreakerModel circuitBreakerModel) {
        if (getData(circuitBreakerModel.getName()) == null) {
            // Store model in outstation data
            outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
                synchronized (outstationData) {
                    // add dnp3 data
                    databaseService.addDoubleBitBinaryInput(ENDPOINT, circuitBreakerModel.getIndexDbbiValue());
                    databaseService.addBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandOpenClose());
                    databaseService.addBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandInvalid());
                    databaseService.addBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandLocalRemote());

                    // add bean data
                    List<Object> dataPoints = Optional
                            .ofNullable(outstationData.getListDataPoints())
                            .orElseGet(ArrayList::new);
                    String cbNewName = PREFIX_NAME + circuitBreakerModel.getName();
                    circuitBreakerModel.setName(cbNewName);
                    dataPoints.add(circuitBreakerModel);

                    outstationData.setListDataPoints(dataPoints);
                }
            });
        }
    }

    public CircuitBreakerModel getData(String name) {
        CircuitBreakerModel circuitBreakerModel = outstationsService.getOutstationData(ENDPOINT)
                .map(OutstationBean.OutstationData::getListDataPoints)
                .orElse(Collections.emptyList())
                .stream()
                .filter(CircuitBreakerModel.class::isInstance)
                .map(CircuitBreakerModel.class::cast)
                .filter(model -> model.getName().equals(PREFIX_NAME + name))
                .findFirst()
                .orElse(null);

        if (circuitBreakerModel != null) {
            // set values
            circuitBreakerModel.setValue(databaseService.getDoubleBitBinaryInput(ENDPOINT, circuitBreakerModel.getIndexDbbiValue()).ordinal());
            circuitBreakerModel.setValueOpenClose(databaseService.getBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandOpenClose()));
            circuitBreakerModel.setValueInvalid(databaseService.getBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandInvalid()));
            circuitBreakerModel.setValueLocalRemote(databaseService.getBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandLocalRemote()));
        }

        return circuitBreakerModel;
    }

    public List<CircuitBreakerModel> getAll() {
        List<CircuitBreakerModel> circuitBreakerModels = outstationsService.getOutstationData(ENDPOINT)
                .map(OutstationBean.OutstationData::getListDataPoints)
                .orElse(Collections.emptyList())
                .stream()
                .filter(CircuitBreakerModel.class::isInstance)
                .map(CircuitBreakerModel.class::cast)
                .toList();

        circuitBreakerModels.forEach(circuitBreakerModel -> {
            circuitBreakerModel.setValue(databaseService.getDoubleBitBinaryInput(ENDPOINT, circuitBreakerModel.getIndexDbbiValue()).ordinal());
            circuitBreakerModel.setValueOpenClose(databaseService.getBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandOpenClose()));
            circuitBreakerModel.setValueInvalid(databaseService.getBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandInvalid()));
            circuitBreakerModel.setValueLocalRemote(databaseService.getBinaryOutput(ENDPOINT, circuitBreakerModel.getIndexBoCommandLocalRemote()));
        });

        return circuitBreakerModels;
    }

    public void deleteData(CircuitBreakerModel circuitBreakerModel) {
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<Object> dataPoints = outstationData.getListDataPoints();

            if (dataPoints != null) {
                // Find the matching CircuitBreakerModel in the actual list
                Optional<CircuitBreakerModel> matchedModelOpt = dataPoints.stream()
                        .filter(CircuitBreakerModel.class::isInstance)
                        .map(CircuitBreakerModel.class::cast)
                        .filter(model -> model.getName().equals(circuitBreakerModel.getName()))
                        .findFirst();

                matchedModelOpt.ifPresent(matchedModel -> {
                    // remove dnp3 data
                    databaseService.deleteDoubleBitBinaryInput(ENDPOINT, matchedModel.getIndexDbbiValue());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandOpenClose());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandInvalid());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandLocalRemote());

                    // remove bean data
                    dataPoints.remove(matchedModel);
                    log.info("Removed model: {}", matchedModel);
                });
            }
        });
    }

    public void actionOpenClose(CircuitBreakerModel circuitBreakerModel) {
        datapointService.operateBinaryOutput(circuitBreakerModel.getValueOpenClose(), UShort.valueOf(circuitBreakerModel.getIndexBoCommandOpenClose()));
    }

    public void actionInvalid(CircuitBreakerModel circuitBreakerModel) {
        datapointService.operateBinaryOutput(circuitBreakerModel.getValueInvalid(), UShort.valueOf(circuitBreakerModel.getIndexBoCommandInvalid()));
    }

    public void actionLocalRemote(CircuitBreakerModel circuitBreakerModel) {
        datapointService.operateBinaryOutput(circuitBreakerModel.getValueLocalRemote(), UShort.valueOf(circuitBreakerModel.getIndexBoCommandLocalRemote()));
    }
}
