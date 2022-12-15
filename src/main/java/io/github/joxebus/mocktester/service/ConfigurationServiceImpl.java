package io.github.joxebus.mocktester.service;

import static io.github.joxebus.mocktester.common.Constants.CREATED_CODE;
import static io.github.joxebus.mocktester.common.Constants.INTERNAL_SERVER_ERROR;
import static io.github.joxebus.mocktester.common.Constants.NOT_FOUND_CODE;
import static io.github.joxebus.mocktester.common.Constants.OK_CODE;
import static io.github.joxebus.mocktester.common.Constants.YAML_EXT;
import static io.github.joxebus.mocktester.utils.MappingUtils.mapApiConfigurationToEndpointConfiguration;
import static io.github.joxebus.mocktester.utils.MappingUtils.mapApiConfigurationToYamlFile;
import static io.github.joxebus.mocktester.utils.MappingUtils.mapYamlFileToApiConfiguration;

import java.io.File;

import org.springframework.stereotype.Service;

import io.github.joxebus.mocktester.model.ApiConfiguration;
import io.github.joxebus.mocktester.model.ApiResponse;
import io.github.joxebus.mocktester.model.EndpointConfiguration;
import io.github.joxebus.mocktester.model.FileResponse;
import io.github.joxebus.mocktester.model.ResponseError;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConfigurationServiceImpl implements ConfigurationService {

    private FileService fileService;

    public ConfigurationServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public ApiResponse createOrUpdateConfiguration(ApiConfiguration apiConfiguration) {
        ApiResponse apiResponse = new ApiResponse();
        // TODO validate ApiConfiguration before save it
        File configuration = mapApiConfigurationToYamlFile(apiConfiguration);
        FileResponse fileResponse = fileService.upload(apiConfiguration.getName() + YAML_EXT, configuration);
        if(fileResponse.isSuccess()) {
            EndpointConfiguration endpointConfiguration = mapApiConfigurationToEndpointConfiguration(apiConfiguration);
            apiResponse.setBody(endpointConfiguration);
            apiResponse.setStatusCode(CREATED_CODE);
        } else {
            apiResponse.setStatusCode(INTERNAL_SERVER_ERROR);
            apiResponse.setBody(fileResponse.getError());
        }
        return apiResponse;
    }

    @Override
    public ApiResponse getConfiguration(String apiName) {
        ApiResponse apiResponse = new ApiResponse();
        FileResponse fileResponse = fileService.download(apiName+YAML_EXT);
        if(fileResponse.isSuccess()) {
            apiResponse.setStatusCode(OK_CODE);
            apiResponse.setBody(mapYamlFileToApiConfiguration(fileResponse.getFile()));
        } else {
            String message = String.format("The configuration [%s] does not exist.", apiName);
            log.warn(message);
            apiResponse.setStatusCode(NOT_FOUND_CODE);
            apiResponse.setBody(ResponseError.newError(message));
        }
        return apiResponse;
    }
}
