package dev.boenkkk.simulator_outstation_dnp3_dynamic.controller;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.CircuitBreakerModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.CircuitBreakerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/")
@Slf4j
public class DashboardController {

    private final CircuitBreakerService circuitBreakerService;

    public DashboardController(CircuitBreakerService circuitBreakerService) {
        this.circuitBreakerService = circuitBreakerService;
    }

    @GetMapping({"", "/dashboard"})
    public ModelAndView index(ModelMap modelMap) {
        List<CircuitBreakerModel> circuitBreakerList = circuitBreakerService.getAll();
        modelMap.addAttribute("circuitBreakerList", circuitBreakerList);
        return new ModelAndView("app/dashboard", modelMap);
    }
}
