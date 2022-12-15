package io.github.joxebus.mocktester.service;

import io.github.joxebus.mocktester.model.ApiResponse;

public interface MockApiService {

    ApiResponse getMockApiOperation(String uri, String method);

}
