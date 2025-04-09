package dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback;

import static org.joou.Unsigned.ushort;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.util.JsonUtil;

import org.joou.UByte;
import org.joou.ULong;
import org.joou.UShort;
import org.springframework.stereotype.Component;

import io.stepfunc.dnp3.ApplicationIin;
import io.stepfunc.dnp3.DatabaseHandle;
import io.stepfunc.dnp3.FreezeResult;
import io.stepfunc.dnp3.FreezeType;
import io.stepfunc.dnp3.OutstationApplication;
import io.stepfunc.dnp3.RestartDelay;
import io.stepfunc.dnp3.StringAttr;
import io.stepfunc.dnp3.WriteTimeResult;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OutstationApplicationImpl implements OutstationApplication {

    private final JsonUtil jsonUtil;

    public OutstationApplicationImpl(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    @Override
    public UShort getProcessingDelayMs() {
        UShort retVal = ushort(0);

        String message = "getProcessingDelayMs retVal:" + retVal;
        log.info(message);

        return retVal;
    }

    @Override
    public WriteTimeResult writeAbsoluteTime(ULong time) {
        WriteTimeResult writeTimeResult = WriteTimeResult.NOT_SUPPORTED;

        String message = "writeAbsoluteTime writeTimeResult:" + writeTimeResult;
        log.info(message);

        return writeTimeResult;
    }

    @Override
    public ApplicationIin getApplicationIin() {
        ApplicationIin applicationIin = new ApplicationIin();
        String jsonApplicationIin = jsonUtil.toJson(applicationIin);

        String message = "getApplicationIin applicationIin:" + jsonApplicationIin;
        log.info(message);

        return new ApplicationIin();
    }

    @Override
    public RestartDelay coldRestart() {
        RestartDelay restartDelay = RestartDelay.seconds(ushort(60));
        String jsonRestartDelay = jsonUtil.toJson(restartDelay);

        String message = "coldRestart restartDelay:" + jsonRestartDelay;
        log.info(message);

        return restartDelay;
    }

    @Override
    public RestartDelay warmRestart() {
        RestartDelay restartDelay = RestartDelay.seconds(ushort(60));
        String jsonRestartDelay = jsonUtil.toJson(restartDelay);

        String message = "warmRestart restartDelay:" + jsonRestartDelay;
        log.info(message);

        return restartDelay;
    }

    @Override
    public FreezeResult freezeCountersAll(FreezeType freezeType, DatabaseHandle databaseHandle) {
        FreezeResult freezeResult = FreezeResult.NOT_SUPPORTED;

        String message = "freezeCountersAll freezeType:" + freezeType + ", databaseHandle:" + databaseHandle + ", freezeResult:" + freezeResult;
        log.info(message);

        return freezeResult;
    }

    @Override
    public FreezeResult freezeCountersRange(UShort start, UShort stop, FreezeType freezeType, DatabaseHandle databaseHandle) {
        FreezeResult freezeResult = FreezeResult.NOT_SUPPORTED;

        String message = "freezeCountersRange start:" + start + ", stop:" + stop + ", freezeType:" + freezeType + ", databaseHandle:" + databaseHandle + ", freezeResult:" + freezeResult;
        log.info(message);

        return freezeResult;
    }

    @Override
    public boolean writeStringAttr(UByte set, UByte variation, StringAttr stringAttr, String value) {
        // Allow writing any string attributes that have been defined as writable
        boolean retVal = true;

        String message = "writeStringAttr set:" + set + ", variation:" + variation + ", stringAttr:" + stringAttr + ", value:" + value + ", retVal:" + retVal;
        log.info(message);

        return true;
    }
}
