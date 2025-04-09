package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import static org.joou.Unsigned.ushort;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.util.TimeUtil;

import java.util.concurrent.atomic.AtomicReference;

import org.joou.UShort;
import org.springframework.stereotype.Service;

import io.stepfunc.dnp3.AnalogInput;
import io.stepfunc.dnp3.AnalogInputConfig;
import io.stepfunc.dnp3.BinaryInput;
import io.stepfunc.dnp3.BinaryInputConfig;
import io.stepfunc.dnp3.BinaryOutputStatus;
import io.stepfunc.dnp3.BinaryOutputStatusConfig;
import io.stepfunc.dnp3.DoubleBit;
import io.stepfunc.dnp3.DoubleBitBinaryInput;
import io.stepfunc.dnp3.DoubleBitBinaryInputConfig;
import io.stepfunc.dnp3.EventAnalogInputVariation;
import io.stepfunc.dnp3.EventBinaryInputVariation;
import io.stepfunc.dnp3.EventBinaryOutputStatusVariation;
import io.stepfunc.dnp3.EventClass;
import io.stepfunc.dnp3.EventDoubleBitBinaryInputVariation;
import io.stepfunc.dnp3.Flag;
import io.stepfunc.dnp3.Flags;
import io.stepfunc.dnp3.StaticAnalogInputVariation;
import io.stepfunc.dnp3.StaticBinaryInputVariation;
import io.stepfunc.dnp3.StaticBinaryOutputStatusVariation;
import io.stepfunc.dnp3.StaticDoubleBitBinaryInputVariation;
import io.stepfunc.dnp3.UpdateOptions;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DatabaseService {

    private final OutstationsService outstationsService;
    private final TimeUtil timeUtil;

    public DatabaseService(OutstationsService outstationsService, TimeUtil timeUtil) {
        this.outstationsService = outstationsService;
        this.timeUtil = timeUtil;
    }

    public void addBinaryInput(String endpoint, Integer index) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                Flags flags = new Flags(Flag.ONLINE);

                BinaryInputConfig binaryInputConfig = new BinaryInputConfig()
                    .withStaticVariation(StaticBinaryInputVariation.GROUP1_VAR2)
                    .withEventVariation(EventBinaryInputVariation.GROUP2_VAR2);
                db.addBinaryInput(ushort(index), EventClass.CLASS1, binaryInputConfig);

                BinaryInput binaryInput = new BinaryInput(ushort(index), false, flags, timeUtil.now());
                db.updateBinaryInput(binaryInput, UpdateOptions.detectEvent());
            }));
    }

    public Boolean getBinaryInput(String endpoint, Integer index) {
        AtomicReference<Boolean> returnValue = new AtomicReference<>(false);
        outstationsService.getOutstationData(endpoint).ifPresent(outstationData -> outstationData.getOutstation()
            .transaction(db -> returnValue.set(db.getBinaryInput(UShort.valueOf(index)).value)));
        return returnValue.get();
    }

    public void updateValueBinaryInput(String endpoint, Integer index, Boolean value) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                Flags flags = new Flags(Flag.ONLINE);

                BinaryInput binaryInput = new BinaryInput(UShort.valueOf(index), value, flags, timeUtil.now());
                db.updateBinaryInput(binaryInput, UpdateOptions.detectEvent());
            }));
    }

    public void deleteBinaryInput(String endpoint, Integer index) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                db.removeBinaryInput(UShort.valueOf(index));
            }));
    }

    public void addBinaryOutput(String endpoint, Integer index) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                Flags flags = new Flags(Flag.ONLINE);
                UpdateOptions updateOptions = UpdateOptions.detectEvent();

                BinaryOutputStatusConfig binaryOutputStatusConfig = new BinaryOutputStatusConfig()
                    .withStaticVariation(StaticBinaryOutputStatusVariation.GROUP10_VAR2)
                    .withEventVariation(EventBinaryOutputStatusVariation.GROUP11_VAR2);
                db.addBinaryOutputStatus(ushort(index), EventClass.CLASS1, binaryOutputStatusConfig);

                BinaryOutputStatus binaryOutputStatus = new BinaryOutputStatus(ushort(index), false, flags,
                    timeUtil.now());
                db.updateBinaryOutputStatus(binaryOutputStatus, updateOptions);
            }));
    }

    public Boolean getBinaryOutput(String endpoint, Integer index) {
        AtomicReference<Boolean> returnValue = new AtomicReference<>(false);
        outstationsService.getOutstationData(endpoint).ifPresent(outstationData -> outstationData.getOutstation()
            .transaction(db -> returnValue.set(db.getBinaryOutputStatus(UShort.valueOf(index)).value)));
        return returnValue.get();
    }

    public void updateValueBinaryOutput(String endpoint, Integer index, Boolean value) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                Flags flags = new Flags(Flag.ONLINE);

                BinaryOutputStatus binaryOutputStatus = new BinaryOutputStatus(UShort.valueOf(index), value, flags,
                    timeUtil.now());
                db.updateBinaryOutputStatus(binaryOutputStatus, UpdateOptions.detectEvent());
            }));
    }

    public void deleteBinaryOutput(String endpoint, Integer index) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                db.removeBinaryOutputStatus(UShort.valueOf(index));
            }));
    }

    public void addAnalogInput(String endpoint, Integer index) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                Flags flags = new Flags(Flag.ONLINE);
                UpdateOptions updateOptions = UpdateOptions.detectEvent();

                AnalogInputConfig analogInputConfig = new AnalogInputConfig()
                    .withStaticVariation(StaticAnalogInputVariation.GROUP30_VAR5)
                    .withEventVariation(EventAnalogInputVariation.GROUP32_VAR7);
                db.addAnalogInput(ushort(index), EventClass.CLASS1, analogInputConfig);

                AnalogInput analogInput = new AnalogInput(ushort(index), 0.0, flags, timeUtil.now());
                db.updateAnalogInput(analogInput, updateOptions);
            }));
    }

    public Double getAnalogInput(String enpoint, Integer index) {
        AtomicReference<Double> returnValue = new AtomicReference<>(0.0);
        outstationsService.getOutstationData(enpoint).ifPresent(outstationData -> outstationData.getOutstation()
            .transaction(db -> returnValue.set(db.getAnalogInput(UShort.valueOf(index)).value)));
        return returnValue.get();
    }

    public void updateValueAnalogInput(String endpoint, Integer index, Double value) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                Flags flags = new Flags(Flag.ONLINE);

                AnalogInput analogInput = new AnalogInput(UShort.valueOf(index), value, flags, timeUtil.now());
                db.updateAnalogInput(analogInput, UpdateOptions.detectEvent());
            }));
    }

    public void deleteAnalogInput(String endpoint, Integer index) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                db.removeAnalogInput(UShort.valueOf(index));
            }));
    }

    public void addDoubleBitBinaryInput(String endpoint, Integer index) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                Flags flags = new Flags(Flag.ONLINE);
                UpdateOptions updateOptions = UpdateOptions.detectEvent();

                DoubleBitBinaryInputConfig doubleBitBinaryInputConfig = new DoubleBitBinaryInputConfig()
                    .withStaticVariation(StaticDoubleBitBinaryInputVariation.GROUP3_VAR2)
                    .withEventVariation(EventDoubleBitBinaryInputVariation.GROUP4_VAR2);
                db.addDoubleBitBinaryInput(ushort(index), EventClass.CLASS1, doubleBitBinaryInputConfig);

                DoubleBitBinaryInput doubleBitBinaryInput = new DoubleBitBinaryInput(ushort(index),
                    DoubleBit.DETERMINED_OFF, flags, timeUtil.now());
                db.updateDoubleBitBinaryInput(doubleBitBinaryInput, updateOptions);
            }));
    }

    public void updateValueDoubleBitBinaryInput(String outstationId, Integer index, DoubleBit value) {
        outstationsService.getOutstationData(outstationId)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                Flags flags = new Flags(Flag.ONLINE);

                DoubleBitBinaryInput doubleBitBinaryInput = new DoubleBitBinaryInput(UShort.valueOf(index), value,
                    flags, timeUtil.now());
                db.updateDoubleBitBinaryInput(doubleBitBinaryInput, UpdateOptions.detectEvent());
            }));
    }

    public DoubleBit getDoubleBitBinaryInput(String enpoint, Integer index) {
        AtomicReference<DoubleBit> returnValue = new AtomicReference<>(DoubleBit.INTERMEDIATE);
        outstationsService.getOutstationData(enpoint).ifPresent(outstationData -> outstationData.getOutstation()
            .transaction(db -> returnValue.set(db.getDoubleBitBinaryInput(UShort.valueOf(index)).value)));
        return returnValue.get();
    }

    public void deleteDoubleBitBinaryInput(String endpoint, Integer index) {
        outstationsService.getOutstationData(endpoint)
            .ifPresent(outstationData -> outstationData.getOutstation().transaction(db -> {
                db.removeDoubleBitBinaryInput(UShort.valueOf(index));
            }));
    }
}
