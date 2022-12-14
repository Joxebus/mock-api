package io.github.joxebus.mocktester.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.joxebus.mocktester.model.ApiConfiguration;
import io.github.joxebus.mocktester.model.FileResponse;
import io.github.joxebus.mocktester.model.Operation;
import io.github.joxebus.mocktester.service.MockApiService;

@Controller
public class MockApiController {

    private MockApiService mockApiService;

    public MockApiController(MockApiService mockApiService) {
        this.mockApiService = mockApiService;
    }

    @RequestMapping("/api/**")
    public ResponseEntity<String> handle(HttpServletRequest request) {
        String uri = request.getRequestURI().substring(7);
        Operation operation = mockApiService.getOperation(uri, request.getMethod());
        return ResponseEntity.status(operation.getStatusCode())
                .body(operation.getBody());
    }

    @PostMapping("/config")
    public ResponseEntity<FileResponse> config(ApiConfiguration apiConfiguration) {
        FileResponse fileResponse = mockApiService.createOrUpdate(apiConfiguration);
        return fileResponse.isSuccess() ? ResponseEntity.ok(fileResponse)
                : ResponseEntity.internalServerError().body(fileResponse);
    }
}
