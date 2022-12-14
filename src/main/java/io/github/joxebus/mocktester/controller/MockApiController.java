package io.github.joxebus.mocktester.controller;

import static io.github.joxebus.mocktester.common.Constants.NOT_FOUND;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.joxebus.mocktester.model.ApiConfiguration;
import io.github.joxebus.mocktester.model.ApiResponse;
import io.github.joxebus.mocktester.model.FileResponse;
import io.github.joxebus.mocktester.service.MockApiService;

@Controller
public class MockApiController {

    private MockApiService mockApiService;

    public MockApiController(MockApiService mockApiService) {
        this.mockApiService = mockApiService;
    }

    @RequestMapping("/api/**")
    public ResponseEntity<String> handle(HttpServletRequest request) {
        String uri = request.getRequestURI().substring(4);
        ApiResponse apiResponse = mockApiService.getOperation(uri, request.getMethod());
        return apiResponse.getStatusCode() == NOT_FOUND ? ResponseEntity.notFound().build():
            ResponseEntity.status(apiResponse.getStatusCode())
                .body(apiResponse.getBody());
    }

    @PostMapping("/config")
    public ResponseEntity<FileResponse> config(@RequestBody ApiConfiguration apiConfiguration) {
        FileResponse fileResponse = mockApiService.createOrUpdate(apiConfiguration);
        return fileResponse.isSuccess() ? ResponseEntity.ok(fileResponse)
                : ResponseEntity.internalServerError().body(fileResponse);
    }

    @GetMapping("/config")
    public ResponseEntity<ApiConfiguration> config(@RequestParam String apiName) {
        ApiConfiguration apiConfiguration = mockApiService.getConfiguration(apiName);
        return Objects.nonNull(apiConfiguration) ? ResponseEntity.ok(apiConfiguration)
                : ResponseEntity.notFound().build();
    }
}
