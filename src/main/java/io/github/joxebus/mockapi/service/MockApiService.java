package io.github.joxebus.mockapi.service;

import io.github.joxebus.mockapi.model.ApiResponse;

public interface MockApiService {

    ApiResponse getMockApiOperation(String uri, String method);

}
