package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeleSignalModel {

    @JsonProperty("name")
    private String name;

    @JsonProperty("index_bi_value")
    private Integer indexBiValue;

    @JsonProperty("index_bo_command_open_close")
    private Integer indexBoCommandOpenClose;

    @JsonProperty("value")
    private Boolean value;

    @JsonProperty("value_open_close")
    private Boolean valueOpenClose;
}
