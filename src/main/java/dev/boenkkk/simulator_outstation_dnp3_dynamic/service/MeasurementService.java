package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.MeasurementModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.scheduler.SchedulerTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MeasurementService {

    private static final String ENDPOINT = "0.0.0.0";
    private static final String PREFIX_NAME = "MEAS_";

    private final DatabaseService databaseService;
    private final OutstationsService outstationsService;
    private final SchedulerTask schedulerTask;

    public MeasurementService(DatabaseService databaseService, OutstationsService outstationsService, SchedulerTask schedulerTask
    ) {
        this.databaseService = databaseService;
        this.outstationsService = outstationsService;
        this.schedulerTask = schedulerTask;
    }

    public void addData(MeasurementModel measurementModel) {
        databaseService.addAnalogInput(ENDPOINT, measurementModel.getIndexAiValue());
        databaseService.addBinaryOutput(ENDPOINT, measurementModel.getIndexBoCommandRaiseLower());
        databaseService.addBinaryOutput(ENDPOINT, measurementModel.getIndexBoCommandAutoManual());

        // Store model in outstation data
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<Object> dataPoints = Optional
                .ofNullable(outstationData.getListDataPoints())
                .orElseGet(ArrayList::new);
            String cbNewName = PREFIX_NAME + measurementModel.getName();
            measurementModel.setName(cbNewName);
            dataPoints.add(measurementModel);

            outstationData.setListDataPoints(dataPoints);
        });

        // scheduler add
        schedulerTask.toggleScheduler(measurementModel.getName(), true, measurementModel.getIntervalScheduler(), measurementModel.getIndexAiValue(), measurementModel.getValueMin(), measurementModel.getValueMax());
    }

    public MeasurementModel getData(String name) {
        return outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getListDataPoints)
            .orElse(Collections.emptyList())
            .stream()
            .filter(MeasurementModel.class::isInstance)
            .map(MeasurementModel.class::cast)
            .filter(model -> model.getName().equals(PREFIX_NAME + name))
            .findFirst()
            .orElse(null);
    }

    public List<MeasurementModel> getAll() {
        return outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getListDataPoints)
            .orElse(Collections.emptyList())
            .stream()
            .filter(MeasurementModel.class::isInstance)
            .map(MeasurementModel.class::cast)
            .toList();
    }

    public void deleteData(MeasurementModel measurementModel) {
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<Object> dataPoints = outstationData.getListDataPoints();

            if (dataPoints != null) {
                // Find the matching model in the actual list
                Optional<MeasurementModel> matchedModelOpt = dataPoints.stream()
                    .filter(MeasurementModel.class::isInstance)
                    .map(MeasurementModel.class::cast)
                    .filter(model -> model.getName().equals(PREFIX_NAME + measurementModel.getName()))
                    .findFirst();

                matchedModelOpt.ifPresent(matchedModel -> {
                    // Remove from original list (important!)
                    dataPoints.remove(matchedModel);
                    log.info("Removed model: {}", matchedModel);

                    // Remove from DNP3 database
                    databaseService.deleteAnalogInput(ENDPOINT, matchedModel.getIndexAiValue());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandRaiseLower());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandAutoManual());
                });
            }
        });
    }
}
