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

    @JsonProperty("index_bi_raise_lower")
    private Integer indexBiRaiseLower;

    @JsonProperty("index_bo_command_auto_manual")
    private Integer indexBoCommandAutoManual;

    @JsonProperty("index_bi_auto_manual")
    private Integer indexBiAutoManual;

    @JsonProperty("index_bo_command_local_remote")
    private Integer indexBoCommandLocalRemote;

    @JsonProperty("index_bi_local_remote")
    private Integer indexBiLocalRemote;

    @JsonProperty("interval_scheduler")
    private Integer intervalScheduler;

    @JsonProperty("value_min")
    private Double valueMin;

    @JsonProperty("value_max")
    private Double valueMax;

    @JsonProperty("value")
    private Integer value;

    @JsonProperty("value_command_raise_lower")
    private Boolean valueCommandRaiseLower;

    @JsonProperty("value_raise_lower")
    private Boolean valueRaiseLower;

    @JsonProperty("value_command_auto_manual")
    private Boolean valueCommandAutoManual;

    @JsonProperty("value_auto_manual")
    private Boolean valueAutoManual;

    @JsonProperty("value_command_local_remote")
    private Boolean valueCommandLocalRemote;

    @JsonProperty("value_local_remote")
    private Boolean valueLocalRemote;

}
