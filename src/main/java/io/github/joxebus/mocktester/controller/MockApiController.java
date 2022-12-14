package io.github.joxebus.mocktester.controller;

import static io.github.joxebus.mocktester.common.Constants.PATH_API;
import static io.github.joxebus.mocktester.common.Constants.PATH_API_LENGTH;
import static io.github.joxebus.mocktester.common.Constants.PATH_WILDCARD;

import java.util.List;
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
import io.github.joxebus.mocktester.model.EndpointConfiguration;
import io.github.joxebus.mocktester.service.MockApiService;

@Controller
public class MockApiController {

    private MockApiService mockApiService;

    public MockApiController(MockApiService mockApiService) {
        this.mockApiService = mockApiService;
    }

    @RequestMapping(value = PATH_API+PATH_WILDCARD, produces = "application/json")
    public ResponseEntity<Object> handle(HttpServletRequest request) {
        String uri = request.getRequestURI().substring(PATH_API_LENGTH);
        ApiResponse apiResponse = mockApiService.getOperation(uri, request.getMethod());
        return buildResponseWithHeaders(apiResponse);
    }

    @PostMapping(value = "/config", produces = "application/json")
    public ResponseEntity<Object> config(@RequestBody ApiConfiguration apiConfiguration) {
        ApiResponse apiResponse = mockApiService.createOrUpdate(apiConfiguration);
        return buildResponseWithHeaders(apiResponse);
    }

    @GetMapping(value = "/config", produces = "application/json")
    public ResponseEntity<ApiConfiguration> config(@RequestParam String apiName) {
        ApiConfiguration apiConfiguration = mockApiService.getConfiguration(apiName);
        return Objects.nonNull(apiConfiguration) ? ResponseEntity.ok(apiConfiguration)
                : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/endpoints", produces = "application/json")
    public ResponseEntity<List<EndpointConfiguration>> endpoints() {
        List<EndpointConfiguration> availableEndpoints = mockApiService.getEndpoints();
        return ResponseEntity.ok(availableEndpoints);
    }

    private ResponseEntity<Object> buildResponseWithHeaders(ApiResponse apiResponse) {
        ResponseEntity.BodyBuilder responseEntityBuilder = ResponseEntity.status(apiResponse.getStatusCode());
        apiResponse.getHeaders().entrySet()
                .forEach( header -> responseEntityBuilder.header(header.getKey(), header.getValue()));
        return responseEntityBuilder.body(apiResponse.getBody());
    }
}
