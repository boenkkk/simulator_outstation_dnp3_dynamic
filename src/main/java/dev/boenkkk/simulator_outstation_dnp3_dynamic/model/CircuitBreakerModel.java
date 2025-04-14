package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CircuitBreakerModel {

    @JsonProperty("name")
    private String name;

    @JsonProperty("index_dbbi_value")
    private Integer indexDbbiValue;

    @JsonProperty("index_bo_command_open_close")
    private Integer indexBoCommandOpenClose;

    @JsonProperty("index_bo_command_invalid")
    private Integer indexBoCommandInvalid;

    @JsonProperty("index_bo_command_local_remote")
    private Integer indexBoCommandLocalRemote;

    @JsonProperty("value")
    private Integer value;

    @JsonProperty("value_open_close")
    private Boolean valueOpenClose;

    @JsonProperty("value_invalid")
    private Boolean valueInvalid;

    @JsonProperty("value_local_remote")
    private Boolean valueLocalRemote;
}
