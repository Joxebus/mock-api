package io.github.joxebus.mocktester.service;

import io.github.joxebus.mocktester.model.ApiConfiguration;
import io.github.joxebus.mocktester.model.ApiResponse;

public interface ConfigurationService {

    ApiResponse createOrUpdateConfiguration(ApiConfiguration apiConfiguration);
    ApiResponse getConfiguration(String apiName);

}
