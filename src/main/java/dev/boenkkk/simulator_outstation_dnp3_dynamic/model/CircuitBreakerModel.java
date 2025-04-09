package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import lombok.Data;

@Data
public class CircuitBreakerModel {

    private String name;
    private Integer indexValue;
    private Integer indexCommandOpenClose;
    private Integer indexCommandInvalid;
    private Integer indexCommandLocalRemote;
}
