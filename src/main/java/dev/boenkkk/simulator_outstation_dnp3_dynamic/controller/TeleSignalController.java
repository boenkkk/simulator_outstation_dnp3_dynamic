package dev.boenkkk.simulator_outstation_dnp3_dynamic.controller;


import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.TeleSignalModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.TeleSignalService;

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
@RequestMapping("/tele-signal")
@Slf4j
public class TeleSignalController {

    private final TeleSignalService teleSignalService;

    public TeleSignalController(TeleSignalService teleSignalService) {
        this.teleSignalService = teleSignalService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<TeleSignalModel>> getAll() {
        List<TeleSignalModel> datas = teleSignalService.getAll();

        return ResponseEntity.ok(datas);
    }

    @GetMapping("/get-data/{name}")
    public ResponseEntity<TeleSignalModel> getData(@PathVariable String name) {
        log.info("req: {}", name);
        TeleSignalModel data = teleSignalService.getData(name);

        return ResponseEntity.ok(data);
    }

    @PostMapping("/add-data")
    public ResponseEntity<List<TeleSignalModel>> addData(@RequestBody TeleSignalModel teleSignalModel) {
        log.info("req: {}", teleSignalModel);
        teleSignalService.addData(teleSignalModel);

        List<TeleSignalModel> datas = teleSignalService.getAll();
        return ResponseEntity.ok(datas);
    }

    @DeleteMapping("/delete-data")
    public ResponseEntity<List<TeleSignalModel>> deleteData(@RequestBody TeleSignalModel teleSignalModel) {
        log.info("req: {}", teleSignalModel);
        teleSignalService.deleteData(teleSignalModel);

        List<TeleSignalModel> datas = teleSignalService.getAll();
        return ResponseEntity.ok(datas);
    }

    @PostMapping("/action-open-close")
    public ResponseEntity<List<TeleSignalModel>> actionOpenClose(@RequestBody TeleSignalModel teleSignalModel) {
        log.info("req: {}", teleSignalModel);
        teleSignalService.actionOpenClose(teleSignalModel);

        List<TeleSignalModel> datas = teleSignalService.getAll();
        return ResponseEntity.ok(datas);
    }
}
