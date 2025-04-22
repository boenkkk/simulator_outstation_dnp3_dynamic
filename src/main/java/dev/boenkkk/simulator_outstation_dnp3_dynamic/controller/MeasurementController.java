package dev.boenkkk.simulator_outstation_dnp3_dynamic.controller;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.MeasurementModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.MeasurementService;

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
@RequestMapping("/measurement")
@Slf4j
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<MeasurementModel>> getAll() {
        List<MeasurementModel> datas = measurementService.getAll();

        return ResponseEntity.ok(datas);
    }

    @GetMapping("/get-data/{name}")
    public ResponseEntity<MeasurementModel> getData(@PathVariable String name) {
        log.info("req: {}", name);
        MeasurementModel data = measurementService.getData(name);

        return ResponseEntity.ok(data);
    }

    @PostMapping("/add-data")
    public ResponseEntity<String> addData(@RequestBody MeasurementModel measurementModel) {
        log.info("req: {}", measurementModel);
        measurementService.addData(measurementModel);

        return ResponseEntity.ok("add-data");
    }

    @DeleteMapping("/delete-data")
    public ResponseEntity<String> deleteData(@RequestBody MeasurementModel measurementModel) {
        log.info("req: {}", measurementModel);
        measurementService.deleteData(measurementModel);

        return ResponseEntity.ok("delete-data");
    }

    @PostMapping("/action-raise-lower")
    public ResponseEntity<List<MeasurementModel>> actionRaiseLower(@RequestBody MeasurementModel measurementModel) {
        log.info("req: {}", measurementModel);
        measurementService.actionRaiseLower(measurementModel);

        List<MeasurementModel> datas = measurementService.getAll();
        return ResponseEntity.ok(datas);
    }

    @PostMapping("/action-auto-manual")
    public ResponseEntity<List<MeasurementModel>> actionAutoManual(@RequestBody MeasurementModel measurementModel) {
        log.info("req: {}", measurementModel);
        measurementService.actionAutoManual(measurementModel);

        List<MeasurementModel> datas = measurementService.getAll();
        return ResponseEntity.ok(datas);
    }
}
