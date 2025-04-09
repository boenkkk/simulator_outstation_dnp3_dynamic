package dev.boenkkk.simulator_outstation_dnp3_dynamic;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.properties.Dnp3Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {Dnp3Properties.class})
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
