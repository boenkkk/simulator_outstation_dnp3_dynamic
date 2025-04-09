package dev.boenkkk.simulator_outstation_dnp3_dynamic.util;

import org.springframework.stereotype.Component;

import io.stepfunc.dnp3.EventAnalogInputVariation;
import io.stepfunc.dnp3.EventAnalogOutputStatusVariation;
import io.stepfunc.dnp3.EventBinaryInputVariation;
import io.stepfunc.dnp3.EventBinaryOutputStatusVariation;
import io.stepfunc.dnp3.EventClass;
import io.stepfunc.dnp3.StaticAnalogInputVariation;
import io.stepfunc.dnp3.StaticAnalogOutputStatusVariation;
import io.stepfunc.dnp3.StaticBinaryInputVariation;
import io.stepfunc.dnp3.StaticBinaryOutputStatusVariation;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ConvertUtil {

    public boolean convertIntToBoolean(int value) {
        if (value == 0) {
            return false;
        } else if (value == 1) {
            return true;
        }

        throw new IllegalArgumentException("Input value only 0 or 1, value: " + value);
    }

    public EventClass getEventClassByName(String name) {
        for (EventClass eventClass : EventClass.values()) {
            if (eventClass.name().equals(name)) {
                return eventClass;
            }
        }

        log.warn("No Type found for: {}", name);
        return EventClass.NONE;
    }

    public StaticAnalogInputVariation getStaticAnalogInputVariationByName(String name) {
        for (StaticAnalogInputVariation staticAnalogInputVariation : StaticAnalogInputVariation.values()) {
            if (staticAnalogInputVariation.name().equals(name)) {
                return staticAnalogInputVariation;
            }
        }

        log.warn("No Type found for: {}", name);
        return StaticAnalogInputVariation.GROUP30_VAR1;
    }

    public EventAnalogInputVariation getEventAnalogInputVariationByName(String name) {
        for (EventAnalogInputVariation eventAnalogInputVariation : EventAnalogInputVariation.values()) {
            if (eventAnalogInputVariation.name().equals(name)) {
                return eventAnalogInputVariation;
            }
        }

        log.warn("No Type found for: {}", name);
        return EventAnalogInputVariation.GROUP32_VAR1;
    }

    public StaticAnalogOutputStatusVariation getStaticAnalogOutputStatusVariationByName(String name) {
        for (StaticAnalogOutputStatusVariation staticAnalogOutputStatusVariation : StaticAnalogOutputStatusVariation.values()) {
            if (staticAnalogOutputStatusVariation.name().equals(name)) {
                return staticAnalogOutputStatusVariation;
            }
        }

        log.warn("No Type found for: {}", name);
        return StaticAnalogOutputStatusVariation.GROUP40_VAR1;
    }

    public EventAnalogOutputStatusVariation getEventAnalogOutputStatusVariationByName(String name) {
        for (EventAnalogOutputStatusVariation eventAnalogOutputStatusVariation : EventAnalogOutputStatusVariation.values()) {
            if (eventAnalogOutputStatusVariation.name().equals(name)) {
                return eventAnalogOutputStatusVariation;
            }
        }

        log.warn("No Type found for: {}", name);
        return EventAnalogOutputStatusVariation.GROUP42_VAR1;
    }

    public StaticBinaryInputVariation getStaticBinaryInputVariationByName(String name) {
        for (StaticBinaryInputVariation staticBinaryInputVariation : StaticBinaryInputVariation.values()) {
            if (staticBinaryInputVariation.name().equals(name)) {
                return staticBinaryInputVariation;
            }
        }

        log.warn("No Type found for: {}", name);
        return StaticBinaryInputVariation.GROUP1_VAR1;
    }

    public EventBinaryInputVariation getEventBinaryInputVariationByName(String name) {
        for (EventBinaryInputVariation eventBinaryInputVariation : EventBinaryInputVariation.values()) {
            if (eventBinaryInputVariation.name().equals(name)) {
                return eventBinaryInputVariation;
            }
        }

        log.warn("No Type found for: {}", name);
        return EventBinaryInputVariation.GROUP2_VAR1;
    }

    public StaticBinaryOutputStatusVariation getStaticBinaryOutputStatusVariationByName(String name) {
        for (StaticBinaryOutputStatusVariation staticBinaryOutputStatusVariation : StaticBinaryOutputStatusVariation.values()) {
            if (staticBinaryOutputStatusVariation.name().equals(name)) {
                return staticBinaryOutputStatusVariation;
            }
        }

        log.warn("No Type found for: {}", name);
        return StaticBinaryOutputStatusVariation.GROUP10_VAR1;
    }

    public EventBinaryOutputStatusVariation getEventBinaryOutputStatusVariationByName(String name) {
        for (EventBinaryOutputStatusVariation eventBinaryOutputStatusVariation : EventBinaryOutputStatusVariation.values()) {
            if (eventBinaryOutputStatusVariation.name().equals(name)) {
                return eventBinaryOutputStatusVariation;
            }
        }

        log.warn("No Type found for: {}", name);
        return EventBinaryOutputStatusVariation.GROUP11_VAR1;
    }
}
