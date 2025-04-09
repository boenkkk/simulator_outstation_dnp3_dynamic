package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import io.stepfunc.dnp3.Outstation;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OutstationsService {

    public static final String OUTSTATION_MAP_BEAN_NAME = "outstationBean";

    private final ApplicationContext appContext;
    private final ConfigurableApplicationContext configContext;

    public OutstationsService(ApplicationContext appContext, ConfigurableApplicationContext configContext) {
        this.appContext = appContext;
        this.configContext = configContext;
    }

    public Optional<OutstationBean.OutstationData> getOutstationData(String endpoint) {
        return Optional.of(getInstance().getData().get(endpoint));
    }

    public OutstationBean getInstance() {
        return (OutstationBean) appContext.getBean(OUTSTATION_MAP_BEAN_NAME);
    }

    public Outstation getOutstation(String endpoint) {
        return getOutstationData(endpoint).map(OutstationBean.OutstationData::getOutstation).orElse(null);
    }

    public void registerBean(OutstationBean outstationBean) {
        DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) configContext.getBeanFactory();
        registry.destroySingleton(OUTSTATION_MAP_BEAN_NAME);
        registry.registerSingleton(OUTSTATION_MAP_BEAN_NAME, outstationBean);
    }

    public Map<String, Object> getAllRunningInstance() {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("total", getInstance().getData().size());
        retMap.put("data", getInstance().getData());

        log.info(retMap.toString());
        return retMap;
    }
}
