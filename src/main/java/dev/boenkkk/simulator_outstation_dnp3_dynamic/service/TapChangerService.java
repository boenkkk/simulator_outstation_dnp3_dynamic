package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.TapChangerModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TapChangerService {

    private static final String ENDPOINT = "0.0.0.0";
    private static final String PREFIX_NAME = "TC_";

    private final DatabaseService databaseService;
    private final OutstationsService outstationsService;

    public TapChangerService(DatabaseService databaseService, OutstationsService outstationsService) {
        this.databaseService = databaseService;
        this.outstationsService = outstationsService;
    }

    public void addData(TapChangerModel tapChangeModel) {
        databaseService.addAnalogInput(ENDPOINT, tapChangeModel.getIndexAiValue());
        databaseService.addBinaryOutput(ENDPOINT, tapChangeModel.getIndexBoCommandRaiseLower());
        databaseService.addBinaryOutput(ENDPOINT, tapChangeModel.getIndexBoCommandAutoManual());
        databaseService.addBinaryOutput(ENDPOINT, tapChangeModel.getIndexBoCommandLocalRemote());

        // Store model in outstation data
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<Object> dataPoints = Optional
                .ofNullable(outstationData.getListDataPoints())
                .orElseGet(ArrayList::new);
            String cbNewName = PREFIX_NAME + tapChangeModel.getName();
            tapChangeModel.setName(cbNewName);
            dataPoints.add(tapChangeModel);

            outstationData.setListDataPoints(dataPoints);
        });
    }

    public TapChangerModel getData(String name) {
        return outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getListDataPoints)
            .orElse(Collections.emptyList())
            .stream()
            .filter(TapChangerModel.class::isInstance)
            .map(TapChangerModel.class::cast)
            .filter(model -> model.equals(PREFIX_NAME + name))
            .findFirst()
            .orElse(null);
    }

    public List<TapChangerModel> getAll() {
        return outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getListDataPoints)
            .orElse(Collections.emptyList())
            .stream()
            .filter(TapChangerModel.class::isInstance)
            .map(TapChangerModel.class::cast)
            .toList();
    }

    public void deleteData(TapChangerModel tapChangerModel) {
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<Object> dataPoints = outstationData.getListDataPoints();

            if (dataPoints != null) {
                // Find the matching model in the actual list
                Optional<TapChangerModel> matchedModelOpt = dataPoints.stream()
                    .filter(TapChangerModel.class::isInstance)
                    .map(TapChangerModel.class::cast)
                    .filter(model -> model.getName().equals(PREFIX_NAME + tapChangerModel.getName()))
                    .findFirst();

                matchedModelOpt.ifPresent(matchedModel -> {
                    // Remove from original list (important!)
                    dataPoints.remove(matchedModel);
                    log.info("Removed model: {}", matchedModel);

                    // Remove from DNP3 database
                    databaseService.deleteAnalogInput(ENDPOINT, matchedModel.getIndexAiValue());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandRaiseLower());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandAutoManual());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandLocalRemote());
                });
            }
        });
    }
}
