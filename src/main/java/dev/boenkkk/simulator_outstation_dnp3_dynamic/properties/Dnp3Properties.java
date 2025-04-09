package dev.boenkkk.simulator_outstation_dnp3_dynamic.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("app.dnp3")
public class Dnp3Properties {

    private int outstationId;
    private String outstationName;
    private String outstationNetworkInterface;
    private String outstationProtocol;

    private String outstationHost;
    private int outstationPort;
    private int outstationAddress;
    private int masterAddress;

    private int keepAliveTimeout;
    private int numCoreThreads;
}
