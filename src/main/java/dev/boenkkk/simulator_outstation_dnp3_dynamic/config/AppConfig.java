package dev.boenkkk.simulator_outstation_dnp3_dynamic.config;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.RuntimeChannel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.properties.Dnp3Properties;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback.LoggerImpl;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback.ShutdownListener;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.util.JsonUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import io.stepfunc.dnp3.LogOutputFormat;
import io.stepfunc.dnp3.Logging;
import io.stepfunc.dnp3.LoggingConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

    private final Dnp3Properties dnp3Properties;
    private final JsonUtil jsonUtil;

    public AppConfig(Dnp3Properties dnp3Properties, @Lazy JsonUtil jsonUtil) {
        this.dnp3Properties = dnp3Properties;
        this.jsonUtil = jsonUtil;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public OutstationBean outstationBean() {
        return new OutstationBean();
    }

    @Bean
    // Setup logging
    public LoggerImpl loggerImpl() {
        return new LoggerImpl(jsonUtil);
    }

    @Bean
    // Create the Tokio runtime
    public RuntimeChannel runtimeChannel(LoggerImpl loggerImpl) {
        LoggingConfig loggingConfig = new LoggingConfig().withOutputFormat(LogOutputFormat.JSON);
        Logging.configure(loggingConfig, loggerImpl);
        return new RuntimeChannel(dnp3Properties.getNumCoreThreads());
    }

    @Bean(destroyMethod = "destroy")
    public ShutdownListener shutdownListener(RuntimeChannel runtimeChannel) {
        return new ShutdownListener(runtimeChannel);
    }
}
