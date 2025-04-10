package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import lombok.Data;

@Data
public class CircuitBreakerModel {

    private String name;
    private Integer indexDbbiValue;
    private Integer indexBoCommandOpenClose;
    private Integer indexBoCommandInvalid;
    private Integer indexBoCommandLocalRemote;
}
