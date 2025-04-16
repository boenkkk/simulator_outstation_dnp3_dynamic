package dev.boenkkk.simulator_outstation_dnp3_dynamic.controller;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.TapChangerModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.TapChangerService;

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
@RequestMapping("/tap-changer")
@Slf4j
public class TapChangerController {

    private final TapChangerService tapChangerService;

    public TapChangerController(TapChangerService tapChangerService) {
        this.tapChangerService = tapChangerService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<TapChangerModel>> getAll() {
        List<TapChangerModel> datas = tapChangerService.getAll();

        return ResponseEntity.ok(datas);
    }

    @GetMapping("/get-data/{name}")
    public ResponseEntity<TapChangerModel> getData(@PathVariable String name) {
        log.info("req: {}", name);
        TapChangerModel data = tapChangerService.getData(name);

        return ResponseEntity.ok(data);
    }

    @PostMapping("/add-data")
    public ResponseEntity<String> addData(@RequestBody TapChangerModel tapChangerModel) {
        log.info("req: {}", tapChangerModel);
        if (tapChangerService.getData(tapChangerModel.getName()) != null) {
            return ResponseEntity.badRequest().body(null);
        }
        tapChangerService.addData(tapChangerModel);

        return ResponseEntity.ok("add-data");
    }

    @DeleteMapping("/delete-data")
    public ResponseEntity<String> deleteData(@RequestBody TapChangerModel tapChangerModel) {
        log.info("req: {}", tapChangerModel);
        tapChangerService.deleteData(tapChangerModel);

        return ResponseEntity.ok("delete-data");
    }

    @PostMapping("/action-raise-lower")
    public ResponseEntity<List<TapChangerModel>> actionRaiseLower(@RequestBody TapChangerModel tapChangerModel) {
        log.info("req: {}", tapChangerModel);
        tapChangerService.actionRaiseLower(tapChangerModel);

        List<TapChangerModel> datas = tapChangerService.getAll();
        return ResponseEntity.ok(datas);
    }

    @PostMapping("/action-auto-manual")
    public ResponseEntity<List<TapChangerModel>> actionAutoManual(@RequestBody TapChangerModel tapChangerModel) {
        log.info("req: {}", tapChangerModel);
        tapChangerService.actionAutoManual(tapChangerModel);

        List<TapChangerModel> datas = tapChangerService.getAll();
        return ResponseEntity.ok(datas);
    }

    @PostMapping("/action-local-remote")
    public ResponseEntity<List<TapChangerModel>> actionLocalRemote(@RequestBody TapChangerModel tapChangerModel) {
        log.info("req: {}", tapChangerModel);
        tapChangerService.actionLocalRemote(tapChangerModel);

        List<TapChangerModel> datas = tapChangerService.getAll();
        return ResponseEntity.ok(datas);
    }
}

