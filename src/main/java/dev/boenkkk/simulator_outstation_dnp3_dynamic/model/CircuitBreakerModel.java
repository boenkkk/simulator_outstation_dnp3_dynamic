package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CircuitBreakerModel {

    private String name;
    private Integer indexDbbiValue;
    private Integer indexBoCommandOpenClose;
    private Integer indexBoCommandInvalid;
    private Integer indexBoCommandLocalRemote;
    private Integer value;
    private Boolean valueLocalRemote;
}
