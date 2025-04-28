package dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback;

import org.joou.UByte;
import org.springframework.stereotype.Component;

import io.stepfunc.dnp3.BroadcastAction;
import io.stepfunc.dnp3.FunctionCode;
import io.stepfunc.dnp3.OutstationInformation;
import io.stepfunc.dnp3.RequestHeader;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OutstationInformationImpl implements OutstationInformation {

    @Override
    public void processRequestFromIdle(RequestHeader requestHeader) {
        // String message = "processRequestFromIdle requestHeader:" + requestHeader;
        // log.info(message);
    }

    @Override
    public void broadcastReceived(FunctionCode functionCode, BroadcastAction broadcastAction) {
        // String message = "broadcastReceived functionCode:" + functionCode + ", broadcastAction:" + broadcastAction;
        // log.info(message);
    }

    @Override
    public void enterSolicitedConfirmWait(UByte ecsn) {
        // String message = "enterSolicitedConfirmWait ecsn:" + ecsn;
        // log.info(message);
    }

    @Override
    public void solicitedConfirmTimeout(UByte ecsn) {
        // String message = "solicitedConfirmTimeout ecsn:" + ecsn;
        // log.info(message);
    }

    @Override
    public void solicitedConfirmReceived(UByte ecsn) {
        // String message = "solicitedConfirmReceived ecsn:" + ecsn;
        // log.info(message);
    }

    @Override
    public void solicitedConfirmWaitNewRequest() {
        // String message = "solicitedConfirmWaitNewRequest";
        // log.info(message);
    }

    @Override
    public void wrongSolicitedConfirmSeq(UByte ecsn, UByte seq) {
        // String message = "wrongSolicitedConfirmSeq ecsn:" + ecsn + ", seq:" + seq;
        // log.info(message);
    }

    @Override
    public void unexpectedConfirm(boolean unsolicited, UByte seq) {
        // String message = "unexpectedConfirm unsolicited:" + unsolicited + ", seq:" + seq;
        // log.info(message);
    }

    @Override
    public void enterUnsolicitedConfirmWait(UByte ecsn) {
        // String message = "enterUnsolicitedConfirmWait ecsn:" + ecsn;
        // log.info(message);
    }

    @Override
    public void unsolicitedConfirmTimeout(UByte ecsn, boolean retry) {
        // String message = "unsolicitedConfirmTimeout ecsn:" + ecsn + ", retry:" + retry;
        // log.info(message);
    }

    @Override
    public void unsolicitedConfirmed(UByte ecsn) {
        // String message = "unsolicitedConfirmed ecsn:" + ecsn;
        // log.info(message);
    }

    @Override
    public void clearRestartIin() {
        // String message = "clearRestartIin";
        // log.info(message);
    }
}
