package dev.boenkkk.simulator_outstation_dnp3_dynamic.controller;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.OutstationBean;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.OutstationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/")
@Slf4j
public class DashboardController {

    private static final String ENDPOINT = "0.0.0.0";

    private final OutstationsService outstationsService;

    public DashboardController(OutstationsService outstationsService) {
        this.outstationsService = outstationsService;
    }

    @GetMapping({"", "/dashboard"})
    public ModelAndView index() {
        return new ModelAndView("app/dashboard");
    }

    @GetMapping("/dashboard/get-bean-data")
    public ResponseEntity<List<Object>> getBeanData() {
        List<Object> objectList = outstationsService.getOutstationData(ENDPOINT)
                .map(OutstationBean.OutstationData::getListDataPoints)
                .orElse(Collections.emptyList());

        return ResponseEntity.ok(objectList);
    }

    @GetMapping("/dashboard/download")
    public ResponseEntity<Object> downloadFile() {
        String filename = "sample-datas.json";
        ClassPathResource resource = new ClassPathResource(filename);

        InputStream inputStream;
        byte[] fileContent;
        try {
            inputStream = resource.getInputStream();
            fileContent = inputStream.readAllBytes();
        } catch (IOException e) {
            return new ResponseEntity<>("file not found", HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileContent.length)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new InputStreamResource(new ByteArrayInputStream(fileContent)));
    }
}
