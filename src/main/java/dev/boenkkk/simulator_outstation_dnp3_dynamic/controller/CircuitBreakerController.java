package dev.boenkkk.simulator_outstation_dnp3_dynamic.controller;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.CircuitBreakerModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.CircuitBreakerService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/circuit-breaker")
@Slf4j
public class CircuitBreakerController {

    private final CircuitBreakerService circuitBreakerService;

    public CircuitBreakerController(CircuitBreakerService circuitBreakerService) {
        this.circuitBreakerService = circuitBreakerService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CircuitBreakerModel>> getAll() {
        List<CircuitBreakerModel> datas = circuitBreakerService.getAll();

        return ResponseEntity.ok(datas);
    }

    @GetMapping("/get-data/{name}")
    public ResponseEntity<CircuitBreakerModel> getData(@PathVariable String name) {
        log.info("req: {}", name);
        CircuitBreakerModel data = circuitBreakerService.getData(name);

        return ResponseEntity.ok(data);
    }

    @PostMapping("/add-data")
    public ResponseEntity<String> addData(@RequestBody CircuitBreakerModel circuitBreakerModel) {
        log.info("req: {}", circuitBreakerModel);
        circuitBreakerService.addData(circuitBreakerModel);

        return ResponseEntity.ok("add-data");
    }

    @DeleteMapping("/delete-data")
    public ResponseEntity<String> deleteData(@RequestBody CircuitBreakerModel circuitBreakerModel) {
        log.info("req: {}", circuitBreakerModel);
        circuitBreakerService.deleteData(circuitBreakerModel);

        return ResponseEntity.ok("delete-data");
    }
}
