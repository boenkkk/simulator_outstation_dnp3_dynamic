package dev.boenkkk.simulator_outstation_dnp3_dynamic.util;

import static org.joou.Unsigned.ulong;

import org.springframework.stereotype.Component;

import io.stepfunc.dnp3.Timestamp;

@Component
public class TimeUtil {

    public Timestamp now() {
        return Timestamp.synchronizedTimestamp(ulong(System.currentTimeMillis()));
    }
}
