package dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback;

import static org.joou.Unsigned.ushort;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.DatapointService;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.util.TimeUtil;

import org.joou.UShort;
import org.springframework.stereotype.Component;

import io.stepfunc.dnp3.AnalogOutputStatus;
import io.stepfunc.dnp3.CommandStatus;
import io.stepfunc.dnp3.ControlHandler;
import io.stepfunc.dnp3.DatabaseHandle;
import io.stepfunc.dnp3.Flag;
import io.stepfunc.dnp3.Flags;
import io.stepfunc.dnp3.Group12Var1;
import io.stepfunc.dnp3.OpType;
import io.stepfunc.dnp3.OperateType;
import io.stepfunc.dnp3.UpdateOptions;

import lombok.extern.slf4j.Slf4j;

// ANCHOR: control_handler
@Component
@Slf4j
public class ControlHandlerImpl implements ControlHandler {

    private final TimeUtil timeUtil;
    private final DatapointService datapointService;

    public ControlHandlerImpl(TimeUtil timeUtil, DatapointService datapointService) {
        this.timeUtil = timeUtil;
        this.datapointService = datapointService;
    }

    @Override
    public void beginFragment() {
        String message = "beginFragment";
        log.info(message);
    }

    @Override
    public void endFragment(DatabaseHandle databaseHandle) {
        String message = "endFragment databaseHandle:" + databaseHandle;
        log.info(message);
    }

    @Override
    public CommandStatus selectG12v1(Group12Var1 group12Var1, UShort index, DatabaseHandle databaseHandle) {
        String message = "selectG12v1 group12Var1:" + group12Var1 + ", index:" + index + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return null;
    }

    @Override
    public CommandStatus operateG12v1(Group12Var1 control, UShort index, OperateType opType, DatabaseHandle database) {
        String message = "operateG12v1 group12Var1:" + control + ", index:" + index + ", opType:" + opType + ", databaseHandle:" + database;
        log.info(message);

        if (control.code.opType == OpType.LATCH_ON || control.code.opType == OpType.LATCH_OFF) {
            return datapointService.operateBinaryOutput(control, index);
        } else {
            return CommandStatus.NOT_SUPPORTED;
        }
    }

    @Override
    public CommandStatus selectG41v1(int value, UShort index, DatabaseHandle databaseHandle) {
        String message = "selectG41v1 value:" + value + ", index:" + index + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v1(int value, UShort index, OperateType opType, DatabaseHandle databaseHandle) {
        String message = "operateG41v1 value:" + value + ", index:" + index + ", opType:" + opType + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return operateAnalogOutput(value, index, databaseHandle);
    }

    @Override
    public CommandStatus selectG41v2(short value, UShort index, DatabaseHandle databaseHandle) {
        String message = "selectG41v2 value:" + value + ", index:" + index + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v2(short value, UShort index, OperateType opType, DatabaseHandle databaseHandle) {
        String message = "operateG41v2 value:" + value + ", index:" + index + ", opType:" + opType + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return operateAnalogOutput(value, index, databaseHandle);
    }

    @Override
    public CommandStatus selectG41v3(float value, UShort index, DatabaseHandle databaseHandle) {
        String message = "selectG41v3 value:" + value + ", index:" + index + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v3(float value, UShort index, OperateType opType, DatabaseHandle databaseHandle) {
        String message = "operateG41v3 value:" + value + ", index:" + index + ", opType:" + opType + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return operateAnalogOutput(value, index, databaseHandle);
    }

    @Override
    public CommandStatus selectG41v4(double value, UShort index, DatabaseHandle databaseHandle) {
        String message = "selectG41v4 value:" + value + ", index:" + index + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v4(double value, UShort index, OperateType opType, DatabaseHandle databaseHandle) {
        String message = "operateG41v4 value:" + value + ", index:" + index + ", opType:" + opType + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return operateAnalogOutput(value, index, databaseHandle);
    }

    private CommandStatus selectAnalogOutput(UShort index) {
        CommandStatus commandStatus = index.compareTo(ushort(10)) < 0 ? CommandStatus.SUCCESS : CommandStatus.NOT_SUPPORTED;

        String message = "selectAnalogOutput index:" + index + ", commandStatus:" + commandStatus;
        log.info(message);

        return commandStatus;
    }

    private CommandStatus operateAnalogOutput(double value, UShort index, DatabaseHandle databaseHandle) {
        CommandStatus commandStatus;
        if (index.compareTo(ushort(10)) < 0) {
            databaseHandle.transaction(
                db -> db.updateAnalogOutputStatus(
                    new AnalogOutputStatus(index, value, new Flags(Flag.ONLINE),
                        timeUtil.now()), UpdateOptions.detectEvent()
                )
            );
            commandStatus = CommandStatus.SUCCESS;
        } else {
            commandStatus = CommandStatus.NOT_SUPPORTED;
        }

        String message = "operateAnalogOutput value:" + value + ", index:" + index + ", databaseHandle:" + databaseHandle + ", commandStatus:" + commandStatus;
        log.info(message);

        return commandStatus;
    }
}
// ANCHOR_END: control_handler
