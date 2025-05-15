package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.TapChangerModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.scheduler.SchedulerTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.joou.UShort;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TapChangerService {

    private static final String ENDPOINT = "0.0.0.0";
    private static final String PREFIX_NAME = "TC_";

    private final DatabaseService databaseService;
    private final OutstationsService outstationsService;
    private final SchedulerTask schedulerTask;
    private final DatapointService datapointService;

    public TapChangerService(DatabaseService databaseService, OutstationsService outstationsService, SchedulerTask schedulerTask, DatapointService datapointService) {
        this.databaseService = databaseService;
        this.outstationsService = outstationsService;
        this.schedulerTask = schedulerTask;
        this.datapointService = datapointService;
    }

    public void addData(TapChangerModel tapChangeModel) {
        if (getData(tapChangeModel.getName()) == null) {
            // Store model in outstation data
            outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
                synchronized (outstationData) {
                    // add dnp3 data
                    databaseService.addAnalogInput(ENDPOINT, tapChangeModel.getIndexAiValue());
                    databaseService.addBinaryOutput(ENDPOINT, tapChangeModel.getIndexBoCommandRaiseLower());
                    databaseService.addBinaryOutput(ENDPOINT, tapChangeModel.getIndexBoCommandAutoManual());
                    databaseService.addBinaryOutput(ENDPOINT, tapChangeModel.getIndexBoCommandLocalRemote());

                    // add bean data
                    List<Object> dataPoints = Optional
                        .ofNullable(outstationData.getListDataPoints())
                        .orElseGet(ArrayList::new);
                    String cbNewName = PREFIX_NAME + tapChangeModel.getName();
                    tapChangeModel.setName(cbNewName);
                    dataPoints.add(tapChangeModel);

                    outstationData.setListDataPoints(dataPoints);
                }
            });

            // add scheduler
            schedulerTask.toggleScheduler(tapChangeModel.getName(), true, tapChangeModel.getIntervalScheduler(), tapChangeModel.getIndexAiValue(), tapChangeModel.getValueMin(), tapChangeModel.getValueMax());
        }
    }

    public TapChangerModel getData(String name) {
        TapChangerModel tapChangerModel = outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getListDataPoints)
            .orElse(Collections.emptyList())
            .stream()
            .filter(TapChangerModel.class::isInstance)
            .map(TapChangerModel.class::cast)
            .filter(model -> model.getName().equals(PREFIX_NAME + name))
            .findFirst()
            .orElse(null);

        if (tapChangerModel != null) {
            // set values
            tapChangerModel.setValue(databaseService.getAnalogInput(ENDPOINT, tapChangerModel.getIndexAiValue()).intValue());
            tapChangerModel.setValueRaiseLower(databaseService.getBinaryOutput(ENDPOINT, tapChangerModel.getIndexBoCommandRaiseLower()));
            tapChangerModel.setValueAutoManual(databaseService.getBinaryOutput(ENDPOINT, tapChangerModel.getIndexBoCommandAutoManual()));
            tapChangerModel.setValueLocalRemote(databaseService.getBinaryOutput(ENDPOINT, tapChangerModel.getIndexBoCommandLocalRemote()));
        }

        return tapChangerModel;
    }

    public List<TapChangerModel> getAll() {
        List<TapChangerModel> tapChangerModels = outstationsService.getOutstationData(ENDPOINT)
            .map(OutstationBean.OutstationData::getListDataPoints)
            .orElse(Collections.emptyList())
            .stream()
            .filter(TapChangerModel.class::isInstance)
            .map(TapChangerModel.class::cast)
            .toList();

        // set values
        tapChangerModels.forEach(tapChangerModel -> {
            tapChangerModel.setValue(databaseService.getAnalogInput(ENDPOINT, tapChangerModel.getIndexAiValue()).intValue());
            tapChangerModel.setValueRaiseLower(databaseService.getBinaryOutput(ENDPOINT, tapChangerModel.getIndexBoCommandRaiseLower()));
            tapChangerModel.setValueAutoManual(databaseService.getBinaryOutput(ENDPOINT, tapChangerModel.getIndexBoCommandAutoManual()));
            tapChangerModel.setValueLocalRemote(databaseService.getBinaryOutput(ENDPOINT, tapChangerModel.getIndexBoCommandLocalRemote()));
        });

        return tapChangerModels;
    }

    public void deleteData(TapChangerModel tapChangerModel) {
        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData -> {
            List<Object> dataPoints = outstationData.getListDataPoints();

            if (dataPoints != null) {
                // Find the matching model in the actual list
                Optional<TapChangerModel> matchedModelOpt = dataPoints.stream()
                    .filter(TapChangerModel.class::isInstance)
                    .map(TapChangerModel.class::cast)
                    .filter(model -> model.getName().equals(tapChangerModel.getName()))
                    .findFirst();

                matchedModelOpt.ifPresent(matchedModel -> {
                    // remove dnp3 data
                    databaseService.deleteAnalogInput(ENDPOINT, matchedModel.getIndexAiValue());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandRaiseLower());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandAutoManual());
                    databaseService.deleteBinaryOutput(ENDPOINT, matchedModel.getIndexBoCommandLocalRemote());

                    // disable scheduler
                    schedulerTask.toggleScheduler(matchedModel.getName(), false, matchedModel.getIntervalScheduler(), matchedModel.getIndexAiValue(), matchedModel.getValueMin(), matchedModel.getValueMax());

                    // remove bean data
                    dataPoints.remove(matchedModel);
                    log.info("Removed model: {}", matchedModel);
                });
            }
        });
    }

    public void actionRaiseLower(TapChangerModel tapChangerModel) {
        datapointService.operateBinaryOutput(tapChangerModel.getValueRaiseLower(), UShort.valueOf(tapChangerModel.getIndexBoCommandRaiseLower()));
    }

    public void actionAutoManual(TapChangerModel tapChangerModel) {
        datapointService.operateBinaryOutput(tapChangerModel.getValueAutoManual(), UShort.valueOf(tapChangerModel.getIndexBoCommandAutoManual()));
    }

    public void actionLocalRemote(TapChangerModel tapChangerModel) {
        datapointService.operateBinaryOutput(tapChangerModel.getValueLocalRemote(), UShort.valueOf(tapChangerModel.getIndexBoCommandLocalRemote()));
    }
}
