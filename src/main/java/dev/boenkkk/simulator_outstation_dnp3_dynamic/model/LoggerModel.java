package dev.boenkkk.simulator_outstation_dnp3_dynamic.model;

import java.util.List;

import lombok.Data;

@Data
public class LoggerModel {

    private String timestamp;
    private String level;

    private LoggerMessageField fields;
    private LoggerSpan span;
    private List<LoggerSpan> spans;

    @Data
    public static class LoggerMessageField {
        private String message;
    }

    @Data
    public static class LoggerSpan {
        private int id;
        private int addr;
        private int ecsn;
        private int seq;
        private String listen;
        private String name;
    }
}
