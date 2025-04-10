package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import lombok.Data;

@Data
public class TapChangerModel {

    private String name;
    private Integer indexAiValue;
    private Integer indexBoCommandRaiseLower;
    private Integer indexBoCommandAutoManual;
    private Integer indexBoCommandLocalRemote;
}
