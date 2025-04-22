package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.MeasurementModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.scheduler.SchedulerTask;
import lombok.extern.slf4j.Slf4j;
import org.joou.UShort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MeasurementService {

    private static final String ENDPOINT = "0.0.0.0";
    private static final String PREFIX_NAME = "MEAS_";

    private final DatabaseService databaseService;
    private final OutstationsService outstationsService;
    private final SchedulerTask schedulerTask;
    private final DatapointService datapointService;

    public MeasurementService(DatabaseService databaseService, OutstationsService outstationsService, SchedulerTask schedulerTask, DatapointService datapointService) {
        this.databaseService = databaseService;
        this.outstationsService = outstationsService;
        this.schedulerTask = schedulerTask;
        this.datapointService = datapointService;
    }

    public void addData(MeasurementModel measurementModel) {
        if (getData(measurementModel.getName()) == null) {
            // Store model in outstation data
            outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
                synchronized (outstationData) {
                    // add dnp3 data
                    databaseService.addAnalogInput(ENDPOINT, measurementModel.getIndexAiValue());
                    databaseService.addBinaryOutput(ENDPOINT, measurementModel.getIndexBoCommandRaiseLower());
                    databaseService.addBinaryOutput(ENDPOINT, measurementModel.getIndexBoCommandAutoManual());

                    // add bean data
                    List<Object> dataPoints = Optional
                            .ofNullable(outstationData.getListDataPoints())
                            .orElseGet(ArrayList::new);
                    String cbNewName = PREFIX_NAME + measurementModel.getName();
                    measurementModel.setName(cbNewName);
                    dataPoints.add(measurementModel);

                    outstationData.setListDataPoints(dataPoints);
                }
            });

            // add scheduler
            schedulerTask.toggleScheduler(measurementModel.getName(), true, measurementModel.getIntervalScheduler(), measurementModel.getIndexAiValue(), measurementModel.getValueMin(), measurementModel.getValueMax());
        }
    }

    public MeasurementModel getData(String name) {
        MeasurementModel measurementModel = outstationsService.getOutstationData(ENDPOINT)
                .map(OutstationBean.OutstationData::getListDataPoints)
                .orElse(Collections.emptyList())
                .stream()
                .filter(MeasurementModel.class::isInstance)
                .map(MeasurementModel.class::cast)
                .filter(model -> model.getName().equals(PREFIX_NAME + name))
                .findFirst()
                .orElse(null);

        if (measurementModel != null) {
            // set values
            measurementModel.setValue(databaseService.getAnalogInput(ENDPOINT, measurementModel.getIndexAiValue()));
            measurementModel.setValueRaiseLower(databaseService.getBinaryOutput(ENDPOINT, measurementModel.getIndexBoCommandRaiseLower()));
            measurementModel.setValueAutoManual(databaseService.getBinaryOutput(ENDPOINT, measurementModel.getIndexBoCommandAutoManual()));
        }

        return measurementModel;
    }

    public List<MeasurementModel> getAll() {
        List<MeasurementModel> measurementModels = outstationsService.getOutstationData(ENDPOINT)
                .map(OutstationBean.OutstationData::getListDataPoints)
                .orElse(Collections.emptyList())
                .stream()
                .filter(MeasurementModel.class::isInstance)
                .map(MeasurementModel.class::cast)
                .toList();

        // set values
        measurementModels.forEach(measurementModel -> {
            measurementModel.setValue(databaseService.getAnalogInput(ENDPOINT, measurementModel.getIndexAiValue()));
            measurementModel.setValueRaiseLower(databaseService.getBinaryOutput(ENDPOINT, measurementModel.getIndexBoCommandRaiseLower()));
            measurementModel.setValueAutoManual(databaseService.getBinaryOutput(ENDPOINT, measurementModel.getIndexBoCommandAutoManual()));
        });

        return measurementModels;
    }

    public void deleteData(MeasurementModel measurementModel) {
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<Object> dataPoints = outstationData.getListDataPoints();

            if (dataPoints != null) {
                // Find the matching model in the actual list
                Optional<MeasurementModel> matchedModelOpt = dataPoints.stream()
                        .filter(MeasurementModel.class::isInstance)
                        .map(MeasurementModel.class::cast)
                        .filter(model -> model.getName().equals(measurementModel.getName()))
                        .findFirst();

                matchedModelOpt.ifPresent(matchedModel -> {
                    // remove dnp3 data
                    databaseService.deleteAnalogInput(ENDPOINT, matchedModel.getIndexAiValue());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandRaiseLower());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandAutoManual());

                    // disable scheduler
                    schedulerTask.toggleScheduler(measurementModel.getName(), true, measurementModel.getIntervalScheduler(), measurementModel.getIndexAiValue(), measurementModel.getValueMin(), measurementModel.getValueMax());

                    // remove bean data
                    dataPoints.remove(matchedModel);
                    log.info("Removed model: {}", matchedModel);
                });
            }
        });
    }

    public void actionRaiseLower(MeasurementModel measurementModel) {
        datapointService.operateBinaryOutput(measurementModel.getValueRaiseLower(), UShort.valueOf(measurementModel.getIndexBoCommandRaiseLower()));
    }

    public void actionAutoManual(MeasurementModel measurementModel) {
        datapointService.operateBinaryOutput(measurementModel.getValueAutoManual(), UShort.valueOf(measurementModel.getIndexBoCommandAutoManual()));
    }
}
