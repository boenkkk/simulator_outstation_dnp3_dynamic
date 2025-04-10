package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.CircuitBreakerModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.MeasurementModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.TapChangerModel;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import org.joou.UShort;
import org.springframework.stereotype.Service;

import io.stepfunc.dnp3.CommandStatus;
import io.stepfunc.dnp3.DatabaseHandle;
import io.stepfunc.dnp3.DoubleBit;
import io.stepfunc.dnp3.Group12Var1;
import io.stepfunc.dnp3.OpType;
import io.stepfunc.dnp3.OperateType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DatapointService {

    private static final String ENDPOINT = "0.0.0.0";

    private final OutstationsService outstationsService;
    private final DatabaseService databaseService;

    public DatapointService(OutstationsService outstationsService, DatabaseService databaseService) {
        this.outstationsService = outstationsService;
        this.databaseService = databaseService;
    }

    public CommandStatus operateBinaryOutput(Group12Var1 control, UShort index, OperateType opType, DatabaseHandle database) {
        AtomicReference<CommandStatus> commandStatus = new AtomicReference<>();
        boolean valueOpType = control.code.opType == OpType.LATCH_ON;

        outstationsService.getOutstationData(ENDPOINT).ifPresent(outstationData ->
            outstationData.getListDataPoints().forEach(obj -> {
                Field[] fields = obj.getClass().getDeclaredFields();

                for (Field field : fields) {
                    if (field.getName().startsWith("indexBo")) {
                        field.setAccessible(true);
                        try {
                            Object value = field.get(obj);
                            if (value instanceof Integer && (Integer) value == index.intValue()) {
                                // Found matching indexBo field
                                String key = field.getName(); // This acts as the key
                                Field nameField = obj.getClass().getDeclaredField("name");
                                nameField.setAccessible(true);
                                String matchingName = (String) nameField.get(obj);

                                log.info("Matched index: {}, Name: {}, Key: {}", index, matchingName, key);

                                // Now do your switch logic
                                if (matchingName.startsWith("CB_")) {
                                    CircuitBreakerModel circuitBreakerModel = (CircuitBreakerModel) obj;

                                    switch (key) {
                                        case "indexBoCommandOpenClose" -> {
                                            log.info("Action: OPEN/CLOSE command for CB_");
                                            databaseService.updateValueBinaryOutput(ENDPOINT, index.intValue(), valueOpType);

                                            DoubleBit doubleBitValue;
                                            if (valueOpType) {
                                                doubleBitValue = DoubleBit.DETERMINED_ON;
                                            } else {
                                                doubleBitValue = DoubleBit.DETERMINED_OFF;
                                            }
                                            databaseService.updateValueDoubleBitBinaryInput(ENDPOINT, circuitBreakerModel.getIndexDbbiValue(), doubleBitValue);

                                            commandStatus.set(CommandStatus.SUCCESS);
                                        }
                                        case "indexBoCommandInvalid" -> {
                                            log.info("Action: INVALID command for CB_");
                                            databaseService.updateValueBinaryOutput(ENDPOINT, index.intValue(), valueOpType);

                                            DoubleBit doubleBitValue;
                                            if (valueOpType) {
                                                doubleBitValue = DoubleBit.INDETERMINATE;
                                            } else {
                                                doubleBitValue = DoubleBit.INTERMEDIATE;
                                            }
                                            databaseService.updateValueDoubleBitBinaryInput(ENDPOINT, circuitBreakerModel.getIndexDbbiValue(), doubleBitValue);

                                            commandStatus.set(CommandStatus.SUCCESS);
                                        }
                                        default -> {
                                            log.warn("Unhandled key for CB_: {}", key);
                                            commandStatus.set(CommandStatus.UNKNOWN);
                                        }
                                    }
                                } else if (matchingName.startsWith("TC_")) {
                                    TapChangerModel tapChangerModel = (TapChangerModel) obj;

                                    switch (key) {
                                        case "indexBoCommandRaiseLower" -> {
                                            log.info("Action: RAISE/LOWER command for TC_");
                                            databaseService.updateValueBinaryOutput(ENDPOINT, index.intValue(), valueOpType);
                                            Double analogInput = databaseService.getAnalogInput(ENDPOINT, tapChangerModel.getIndexAiValue());
                                            double updateValue;
                                            if (valueOpType) {
                                                updateValue = analogInput + 1.0;
                                                updateValue = updateValue >= 32 ? 32 : updateValue;
                                                databaseService.updateValueAnalogInput(ENDPOINT, tapChangerModel.getIndexAiValue(), updateValue);
                                            } else {
                                                updateValue = analogInput - 1.0;
                                                updateValue = updateValue <= 0 ? 0 : updateValue;
                                                databaseService.updateValueAnalogInput(ENDPOINT, tapChangerModel.getIndexAiValue(), updateValue);
                                            }

                                            commandStatus.set(CommandStatus.SUCCESS);
                                        }
                                        default -> {
                                            log.warn("Unhandled key for TC_: {}", key);
                                            commandStatus.set(CommandStatus.UNKNOWN);
                                        }
                                    }
                                } else if (matchingName.startsWith("MEAS_")) {
                                    MeasurementModel measurementModel = (MeasurementModel) obj;

                                    switch (key) {
                                        case "indexBoCommandAutoManual" -> {
                                            log.info("Action: AUTO/MANUAL command for MEAS_");
                                            // your logic here

                                            commandStatus.set(CommandStatus.SUCCESS);
                                        }
                                        default -> {
                                            log.warn("Unhandled key for MEAS_: {}", key);
                                            commandStatus.set(CommandStatus.UNKNOWN);
                                        }
                                    }
                                } else {
                                    log.warn("No matching prefix found for name: {}", matchingName);
                                    commandStatus.set(CommandStatus.UNKNOWN);
                                }
                            }
                        } catch (IllegalAccessException | NoSuchFieldException e) {
                            log.error("Reflection error: ", e);
                            commandStatus.set(CommandStatus.UNKNOWN);
                        }
                    }
                }
            })
        );

        return commandStatus.get();
    }
}
