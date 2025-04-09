package dev.boenkkk.simulator_outstation_dnp3_dynamic.run;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.Dnp3ServerOutstationModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.properties.Dnp3Properties;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.ServerService;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OutstationRunner implements ApplicationRunner {

    private final Dnp3Properties dnp3Properties;
    private final ServerService serverService;

    public OutstationRunner(Dnp3Properties dnp3Properties, ServerService serverService) {
        this.dnp3Properties = dnp3Properties;
        this.serverService = serverService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // get outstations
        Dnp3ServerOutstationModel outstation = new Dnp3ServerOutstationModel();
        outstation.setTcpSourceIpAddress(dnp3Properties.getOutstationHost());
        outstation.setTcpPortNumber(dnp3Properties.getOutstationPort());
        outstation.setSlaveAddress(dnp3Properties.getOutstationAddress());
        outstation.setMasterAddress(dnp3Properties.getMasterAddress());
        log.info("outstation: {}", outstation);

        // add server
        serverService.addServer(outstation);
    }
}
