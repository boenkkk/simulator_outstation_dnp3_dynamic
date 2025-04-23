package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.TeleSignalModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.scheduler.SchedulerTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TeleSignalService {

    private static final String ENDPOINT = "0.0.0.0";
    private static final String PREFIX_NAME = "TS_";

    private final DatabaseService databaseService;
    private final OutstationsService outstationsService;
    private final SchedulerTask schedulerTask;
    private final DatapointService datapointService;

    public TeleSignalService(DatabaseService databaseService, OutstationsService outstationsService, SchedulerTask schedulerTask, DatapointService datapointService) {
        this.databaseService = databaseService;
        this.outstationsService = outstationsService;
        this.schedulerTask = schedulerTask;
        this.datapointService = datapointService;
    }

    public TeleSignalModel getData(String name) {
        TeleSignalModel teleSignalModel = outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getListDataPoints)
            .orElse(Collections.emptyList())
            .stream()
            .filter(TeleSignalModel.class::isInstance)
            .map(TeleSignalModel.class::cast)
            .filter(model -> model.getName().equals(PREFIX_NAME + name))
            .findFirst()
            .orElse(null);

        if (teleSignalModel != null) {
            // set values
            teleSignalModel.setValue(databaseService.getBinaryInput(ENDPOINT, teleSignalModel.getIndexBiValue()));
            teleSignalModel.setValueOpenClose(databaseService.getBinaryOutput(ENDPOINT, teleSignalModel.getIndexBoCommandOpenClose()));
        }

        return teleSignalModel;
    }

    public List<TeleSignalModel> getAll() {
        List<TeleSignalModel> teleSignalModels = outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getListDataPoints)
            .orElse(Collections.emptyList())
            .stream()
            .filter(TeleSignalModel.class::isInstance)
            .map(TeleSignalModel.class::cast)
            .toList();

        teleSignalModels.forEach(teleSignalModel -> {
            teleSignalModel.setValue(databaseService.getBinaryInput(ENDPOINT, teleSignalModel.getIndexBiValue()));
            teleSignalModel.setValueOpenClose(databaseService.getBinaryOutput(ENDPOINT, teleSignalModel.getIndexBoCommandOpenClose()));
        });

        return teleSignalModels;
    }

    public void addData(TeleSignalModel teleSignalModel) {
        if (getData(teleSignalModel.getName()) == null) {
            // Store model in outstation data
            outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
                synchronized (outstationData) {
                    // add dnp3 data
                    databaseService.addBinaryInput(ENDPOINT, teleSignalModel.getIndexBiValue());
                    databaseService.addBinaryOutput(ENDPOINT, teleSignalModel.getIndexBoCommandOpenClose());

                    // add bean data
                    List<Object> dataPoints = Optional
                        .ofNullable(outstationData.getListDataPoints())
                        .orElseGet(ArrayList::new);
                    String cbNewName = PREFIX_NAME + teleSignalModel.getName();
                    teleSignalModel.setName(cbNewName);
                    dataPoints.add(teleSignalModel);

                    outstationData.setListDataPoints(dataPoints);
                }
            });
        }
    }

    public void deleteData(TeleSignalModel teleSignalModel) {
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<Object> dataPoints = outstationData.getListDataPoints();

            if (dataPoints != null) {
                // Find the matching model in the actual list
                Optional<TeleSignalModel> matchedModelOpt = dataPoints.stream()
                    .filter(TeleSignalModel.class::isInstance)
                    .map(TeleSignalModel.class::cast)
                    .filter(model -> model.getName().equals(teleSignalModel.getName()))
                    .findFirst();

                matchedModelOpt.ifPresent(matchedModel -> {
                    // remove dnp3 data
                    databaseService.deleteBinaryInput(ENDPOINT, matchedModel.getIndexBiValue());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandOpenClose());

                    // remove bean data
                    dataPoints.remove(matchedModel);
                    log.info("Removed model: {}", matchedModel);
                });
            }
        });
    }
}
