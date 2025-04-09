package dev.boenkkk.simulator_outstation_dnp3_dynamic.service;

import static org.joou.Unsigned.ushort;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.config.DatabaseConfigImpl;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.Dnp3ServerOutstationModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean.OutstationData;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.RuntimeChannel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.properties.Dnp3Properties;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback.ConnectionStateListenerImpl;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback.ControlHandlerImpl;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback.OutstationApplicationImpl;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback.OutstationInformationImpl;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.util.HttpUtil;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.util.JsonUtil;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.util.TimeUtil;

import java.time.Duration;

import org.springframework.stereotype.Service;

import io.stepfunc.dnp3.AddressFilter;
import io.stepfunc.dnp3.AppDecodeLevel;
import io.stepfunc.dnp3.EventBufferConfig;
import io.stepfunc.dnp3.LinkDecodeLevel;
import io.stepfunc.dnp3.LinkErrorMode;
import io.stepfunc.dnp3.Outstation;
import io.stepfunc.dnp3.OutstationConfig;
import io.stepfunc.dnp3.OutstationServer;
import io.stepfunc.dnp3.PhysDecodeLevel;
import io.stepfunc.dnp3.TransportDecodeLevel;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServerService {

    private final RuntimeChannel runtimeChannel;
    private final Dnp3Properties dnp3Properties;
    private final OutstationsService outstationsService;
    private final DatabaseConfigImpl databaseConfigImpl;
    private final JsonUtil jsonUtil;
    private final TimeUtil timeUtil;
    private final HttpUtil httpUtil;

    public ServerService(
        RuntimeChannel runtimeChannel, Dnp3Properties dnp3Properties, OutstationsService outstationsService,
        DatabaseConfigImpl databaseConfigImpl, JsonUtil jsonUtil, TimeUtil timeUtil, HttpUtil httpUtil
    ) {
        this.runtimeChannel = runtimeChannel;
        this.dnp3Properties = dnp3Properties;
        this.outstationsService = outstationsService;
        this.databaseConfigImpl = databaseConfigImpl;
        this.jsonUtil = jsonUtil;
        this.timeUtil = timeUtil;
        this.httpUtil = httpUtil;
    }

    public void addServer(Dnp3ServerOutstationModel dnp3ServerOutstation) {
        log.info(dnp3ServerOutstation.toString());
        String outstationTcpAddress = dnp3ServerOutstation.getTcpSourceIpAddress() + ":" + dnp3ServerOutstation.getTcpPortNumber();
        String endpoint = dnp3ServerOutstation.getTcpSourceIpAddress();

        // ANCHOR: create_tcp_server
        OutstationServer server = OutstationServer.createTcpServer(
            runtimeChannel.getRuntime(),
            LinkErrorMode.CLOSE,
            outstationTcpAddress
        );
        // ANCHOR_END: create_tcp_server

        // ANCHOR: outstation_config
        // create an outstation configuration with default values
        // int maxEventBuffer = 50;
        OutstationConfig outstationConfig = new OutstationConfig(
            ushort(dnp3ServerOutstation.getSlaveAddress()),
            ushort(dnp3ServerOutstation.getMasterAddress()),
            // event buffer sizes
            new EventBufferConfig(
                ushort(50), // binary
                ushort(50), // double-bit binary
                ushort(50), // binary output status
                ushort(5), // counter
                ushort(5), // frozen counter
                ushort(50), // analog
                ushort(50), // analog output status
                ushort(3) // octet string
            )
        ).withKeepAliveTimeout(Duration.ofSeconds(dnp3Properties.getKeepAliveTimeout()));
        outstationConfig.decodeLevel.application = AppDecodeLevel.NOTHING;
        outstationConfig.decodeLevel.transport = TransportDecodeLevel.NOTHING;
        outstationConfig.decodeLevel.link = LinkDecodeLevel.NOTHING;
        outstationConfig.decodeLevel.physical = PhysDecodeLevel.NOTHING;
        // ANCHOR_END: outstation_config

        // ANCHOR: tcp_server_add_outstation
        Outstation outstation = server.addOutstation(
            outstationConfig,
            new OutstationApplicationImpl(jsonUtil),
            new OutstationInformationImpl(),
            new ControlHandlerImpl(timeUtil),
            new ConnectionStateListenerImpl(dnp3Properties, httpUtil),
            AddressFilter.any()
        );
        // ANCHOR_END: tcp_server_add_outstation

        // ANCHOR: tcp_server_bind
        server.bind();
        // ANCHOR_END: tcp_server_bind

        // Setup initial points
        // ANCHOR: database_init
        outstation.transaction(databaseConfigImpl::initializeDatabaseDefault);
        // ANCHOR_END: database_init

        // automaticly started channel
        outstation.enable();

        // register to bean
        OutstationBean outstationBean = outstationsService.getInstance();
        outstationBean.getData().put(
            endpoint,
            OutstationData.builder()
                .outstationServer(server)
                .outstation(outstation)
                .circuitBreakerModel(null)
                .build()
        );
        outstationsService.registerBean(outstationBean);
    }
}
