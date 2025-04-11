package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import lombok.Data;

@Data
public class MeasurementModel {

    private String name;
    private Integer indexAiValue;
    private Integer indexBoCommandRaiseLower;
    private Integer indexBoCommandAutoManual;
    private Integer intervalScheduler;
    private Double valueMin;
    private Double valueMax;
}
