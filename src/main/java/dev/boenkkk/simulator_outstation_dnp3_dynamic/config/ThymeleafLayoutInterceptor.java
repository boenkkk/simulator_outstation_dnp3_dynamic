package dev.boenkkk.simulator_outstation_dnp3_dynamic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class ThymeleafLayoutInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    private static final String DEFAULT_LAYOUT = "fragments/layout";
    private static final String DEFAULT_VIEW_ATTRIBUTE_NAME = "view";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

        if (modelAndView == null || !modelAndView.hasView()) {
            return;
        }
        String originalViewName = modelAndView.getViewName();

        if (originalViewName.startsWith("redirect:")) {
            return;
        }

        if (request.getRequestURI().startsWith("/error403")
            || request.getRequestURI().startsWith("/error404")
            || request.getRequestURI().startsWith("/error500")) {
            // Skip layout processing for specific paths
        } else {
            modelAndView.setViewName(DEFAULT_LAYOUT);
        }

        modelAndView.addObject(DEFAULT_VIEW_ATTRIBUTE_NAME, originalViewName);
    }
}
