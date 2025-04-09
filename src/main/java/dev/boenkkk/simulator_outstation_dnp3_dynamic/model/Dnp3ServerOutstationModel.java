package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import lombok.Data;

@Data
public class Dnp3ServerOutstationModel {

    private String tcpSourceIpAddress;
    private Integer tcpPortNumber;
    private Integer slaveAddress;
    private Integer masterAddress;
}
