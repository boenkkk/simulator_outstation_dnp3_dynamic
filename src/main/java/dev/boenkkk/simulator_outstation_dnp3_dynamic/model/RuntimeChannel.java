package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import static org.joou.Unsigned.ushort;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import io.stepfunc.dnp3.Runtime;
import io.stepfunc.dnp3.RuntimeConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class RuntimeChannel {

    private Runtime runtime;

    public RuntimeChannel(int numCoreThreads) {
        RuntimeConfig runtimeConfig =
            new RuntimeConfig().withNumCoreThreads(ushort(numCoreThreads > 0 ? numCoreThreads : 4));
        runtime = new Runtime(runtimeConfig);
        dispose(true);
    }

    public void dispose(boolean dispose) {
        try {
            Field f1 = runtime.getClass().getDeclaredField("disposed");
            f1.setAccessible(true);
            f1.set(runtime, new AtomicBoolean(dispose));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
