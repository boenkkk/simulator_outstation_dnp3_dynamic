package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TapChangerModel {

    @JsonProperty("name")
    private String name;

    @JsonProperty("index_ai_value")
    private Integer indexAiValue;

    @JsonProperty("index_bo_command_raise_lower")
    private Integer indexBoCommandRaiseLower;

    @JsonProperty("index_bo_command_auto_manual")
    private Integer indexBoCommandAutoManual;

    @JsonProperty("index_bo_command_local_remote")
    private Integer indexBoCommandLocalRemote;

    @JsonProperty("interval_scheduler")
    private Integer intervalScheduler;

    @JsonProperty("value_min")
    private Double valueMin;

    @JsonProperty("value_max")
    private Double valueMax;

    @JsonProperty("value")
    private Integer value;

    @JsonProperty("value_raise_lower")
    private Boolean valueRaiseLower;

    @JsonProperty("value_auto_manual")
    private Boolean valueAutoManual;

    @JsonProperty("value_local_remote")
    private Boolean valueLocalRemote;

}
