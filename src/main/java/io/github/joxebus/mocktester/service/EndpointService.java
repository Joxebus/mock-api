package io.github.joxebus.mocktester.service;

import io.github.joxebus.mocktester.model.ApiResponse;

public interface EndpointService {

    ApiResponse getEndpoints();
    ApiResponse getEndpoint(String apiName);
}
