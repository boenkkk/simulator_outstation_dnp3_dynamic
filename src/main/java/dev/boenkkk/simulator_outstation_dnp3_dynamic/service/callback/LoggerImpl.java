package dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.LoggerModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.util.JsonUtil;

import org.springframework.stereotype.Component;

import io.stepfunc.dnp3.LogLevel;
import io.stepfunc.dnp3.Logger;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggerImpl implements Logger {

    private final JsonUtil jsonUtil;

    public LoggerImpl(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void onMessage(LogLevel level, String message) {
        try {
            // Sanitize: replace "\n" and revome char "\"
            String sanitizedMessage = message.replaceAll("\\\\n", " ").replaceAll("\\\\\"", "");
            LoggerModel loggerModel = jsonUtil.fromJson(sanitizedMessage, LoggerModel.class);

            log.info(loggerModel.toString());
        } catch (Exception e) {
            log.error("message:{}, error:{}", message, e.getMessage());
        }
    }
}
