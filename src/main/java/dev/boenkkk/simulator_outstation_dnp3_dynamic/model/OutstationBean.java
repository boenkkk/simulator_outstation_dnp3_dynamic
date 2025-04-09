package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.stepfunc.dnp3.Outstation;
import io.stepfunc.dnp3.OutstationServer;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OutstationBean {

    private final Map<String, OutstationData> data = new HashMap<>();

    @Data
    @Builder
    public static class OutstationData {

        private final OutstationServer outstationServer;
        private final Outstation outstation;
        private List<CircuitBreakerModel> circuitBreakerModel;
    }
}
