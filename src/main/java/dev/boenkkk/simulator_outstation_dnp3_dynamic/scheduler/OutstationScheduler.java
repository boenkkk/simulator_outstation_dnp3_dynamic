package dev.boenkkk.simulator_outstation_dnp3_dynamic.scheduler;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.OutstationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class OutstationScheduler {

    private final OutstationsService outstationsService;

    public OutstationScheduler(OutstationsService outstationsService) {
        this.outstationsService = outstationsService;
    }

    @Scheduled(fixedRate = 1000)
    public void getAllRunningInstance() {
        outstationsService.getAllRunningInstance();
    }
}
