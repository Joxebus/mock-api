package io.github.joxebus.mocktester.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.joxebus.mocktester.model.ApiResponse;

@Controller
public class MockApiController {

    @RequestMapping("/api/**")
    public ResponseEntity<ApiResponse> handle(HttpServletRequest request, HttpServletResponse response) {
        String requestUri = request.getRequestURI().substring(7);
        ApiResponse apiResponse = new ApiResponse(requestUri, request.getParameterMap());
        return ResponseEntity.ok(apiResponse);
    }
}
