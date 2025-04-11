package dev.boenkkk.simulator_outstation_dnp3_dynamic.scheduler;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.DatabaseService;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.util.RandomUtil;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SchedulerTask {

    // Store active schedulers
    private final Map<String, SchedulerInstance> schedulers = new ConcurrentHashMap<>();

    private final RandomUtil randomUtil;
    private final DatabaseService databaseService;
    private final TaskScheduler taskScheduler;

    public SchedulerTask(DatabaseService databaseService, RandomUtil randomUtil, @Lazy TaskScheduler taskScheduler) {
        this.databaseService = databaseService;
        this.randomUtil = randomUtil;
        this.taskScheduler = taskScheduler;
    }

    /**
     * Configure task scheduler bean
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("scheduler-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.initialize();
        return scheduler;
    }

    /**
     * Create or toggle a scheduler
     *
     * @param key      Unique identifier for the scheduler
     * @param enable   Whether to enable or disable the scheduler
     * @param interval Interval in seconds between task executions
     * @param index    Analog input index
     * @param minValue Minimum random value
     * @param maxValue Maximum random value
     */
    public synchronized void toggleScheduler(
        String key,
        boolean enable,
        int interval,
        int index,
        double minValue,
        double maxValue
    ) {
        if (enable) {
            // Stop existing scheduler if it exists
            SchedulerInstance existingScheduler = schedulers.get(key);
            if (existingScheduler != null) {
                existingScheduler.stop();
                schedulers.remove(key);
            }

            // Create new scheduler instance
            SchedulerInstance scheduler = new SchedulerInstance(key, index, minValue, maxValue);

            // Schedule the task
            scheduler.scheduledTask = taskScheduler.scheduleAtFixedRate(
                scheduler::execute,
                Duration.ofSeconds(interval)
            );

            // Store the scheduler
            schedulers.put(key, scheduler);

            log.info("Scheduler {} enabled with interval: {} seconds.", key, interval);
        } else {
            // Disable and remove scheduler
            SchedulerInstance scheduler = schedulers.get(key);
            if (scheduler != null) {
                scheduler.stop();
                schedulers.remove(key);
                log.info("Scheduler {} disabled.", key);
            }
        }
    }

    /**
     * Get the number of active schedulers
     *
     * @return Number of active schedulers
     */
    public int getActiveSchedulersCount() {
        return schedulers.size();
    }

    /**
     * Shutdown all schedulers
     */
    public void shutdownAll() {
        schedulers.values().forEach(SchedulerInstance::stop);
        schedulers.clear();
    }

    /**
     * Inner class to represent a single scheduler instance
     */
    private class SchedulerInstance {
        private final String key;
        private final int index;
        private final double minValue;
        private final double maxValue;
        private ScheduledFuture<?> scheduledTask;

        public SchedulerInstance(String key, int index, double minValue, double maxValue) {
            this.key = key;
            this.index = index;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public void execute() {
            try {
                databaseService.updateValueAnalogInput(
                    "0.0.0.0",
                    index,
                    randomUtil.getRandomDouble(minValue, maxValue)
                );
            } catch (Exception e) {
                log.error("Error executing task for scheduler {}: {}", key, e.getMessage(), e);
            }
        }

        public void stop() {
            if (scheduledTask != null) {
                scheduledTask.cancel(true);
            }
        }
    }
}
