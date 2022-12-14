package io.github.joxebus.mocktester.service;

import static io.github.joxebus.mocktester.common.Constants.INTERNAL_SERVER_ERROR;
import static io.github.joxebus.mocktester.common.Constants.METHOD_NOT_ALLOWED;
import static io.github.joxebus.mocktester.common.Constants.CREATED_CODE;
import static io.github.joxebus.mocktester.common.Constants.NOT_FOUND_CODE;
import static io.github.joxebus.mocktester.common.Constants.PATH_API;
import static io.github.joxebus.mocktester.common.Constants.PATH_CONFIG;
import static io.github.joxebus.mocktester.common.Constants.SLASH;
import static io.github.joxebus.mocktester.common.Constants.YAML_EXT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import io.github.joxebus.mocktester.model.ApiConfiguration;
import io.github.joxebus.mocktester.model.ApiOperation;
import io.github.joxebus.mocktester.model.ApiResponse;
import io.github.joxebus.mocktester.model.Endpoint;
import io.github.joxebus.mocktester.model.EndpointConfiguration;
import io.github.joxebus.mocktester.model.FileResponse;
import io.github.joxebus.mocktester.model.ResponseError;
import io.github.joxebus.mocktester.service.common.FileService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MockApiService {

    private FileService fileService;

    public MockApiService(FileService fileService) {
        this.fileService = fileService;
    }

    public ApiResponse createOrUpdate(ApiConfiguration apiConfiguration) {
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

    public ApiResponse getOperation(String uri, String method) {
        String[] uriParts = uri.substring(1).split(SLASH);
        String fileName = uriParts[0];
        String operationName = uriParts[1];
        FileResponse fileResponse = fileService.download(fileName+YAML_EXT);
        ApiResponse apiResponse = null;
        if(fileResponse.isSuccess()) {
            ApiConfiguration apiConfiguration = mapYamlFileToApiConfiguration(fileResponse.getFile());
            apiResponse = apiConfigurationToApiResponse(apiConfiguration, operationName, method);
        } else {
            String message = String.format("Configuration not found for URI [%s]", uri);
            log.warn(message);
            apiResponse = new ApiResponse();
            apiResponse.setStatusCode(NOT_FOUND_CODE);
            apiResponse.setBody(ResponseError.newError(message));
        }
        return apiResponse;
    }

    public List<EndpointConfiguration> getEndpoints() {
        List<String> fileNames = fileService.filesWithExtension(YAML_EXT);
        List<EndpointConfiguration> endpointConfigurations = new ArrayList<>();
        fileNames.stream().parallel()
                .forEach(filename -> {
                    FileResponse fileResponse = fileService.download(filename);
                    ApiConfiguration apiConfiguration = mapYamlFileToApiConfiguration(fileResponse.getFile());
                    endpointConfigurations.add(mapApiConfigurationToEndpointConfiguration(apiConfiguration));
                });

        return endpointConfigurations;

    }

    // Helper Methods

    private EndpointConfiguration mapApiConfigurationToEndpointConfiguration(ApiConfiguration apiConfiguration) {
        String config = PATH_CONFIG + "?apiName=" + apiConfiguration.getName();
        List<Endpoint> endpoints = apiConfiguration.getOperations().entrySet().stream()
                .map(entry -> buildEndpointFromEntry(apiConfiguration.getName(), entry)).collect(Collectors.toList());
        return new EndpointConfiguration(config, endpoints);
    }

    private Endpoint buildEndpointFromEntry(String apiName, Map.Entry<String, ApiOperation> entry) {
        String href = PATH_API + SLASH + apiName + SLASH + entry.getKey();
        String method = entry.getValue().getMethod().toUpperCase();
        int statusCode = entry.getValue().getStatusCode();
        return new Endpoint(href, method, statusCode);
    }

    private ApiResponse apiConfigurationToApiResponse(ApiConfiguration apiConfiguration,
                                                      String operationName, String method) {
        ApiResponse apiResponse = new ApiResponse();

        if(Objects.isNull(apiConfiguration) || !apiConfiguration.getOperations().containsKey(operationName)) {
            String message = String.format("There are no configuration for operation [%s]", operationName);
            log.warn(message);
            apiResponse.setStatusCode(NOT_FOUND_CODE);
            apiResponse.setBody(ResponseError.newError(message));
            return apiResponse;
        }

        ApiOperation operation = apiConfiguration.getOperations().get(operationName);
        if(method.equalsIgnoreCase(operation.getMethod())) {
            apiResponse.setHeaders(operation.getHeaders());
            apiResponse.setStatusCode(operation.getStatusCode());
            apiResponse.setBody(operation.getBody());
        } else {
            String message = String.format("METHOD NOT ALLOWED for operation [%s]", operationName);
            apiResponse.setStatusCode(METHOD_NOT_ALLOWED);
            apiResponse.setBody(ResponseError.newError(message));
        }

        return apiResponse;
    }

    public ApiConfiguration getConfiguration(String apiName) {
        ApiConfiguration apiConfiguration = null;
        FileResponse fileResponse = fileService.download(apiName+YAML_EXT);
        if(fileResponse.isSuccess()) {
            apiConfiguration = mapYamlFileToApiConfiguration(fileResponse.getFile());
        }
        return apiConfiguration;
    }

    private ApiConfiguration mapYamlFileToApiConfiguration(File yamlSource) {
        ApiConfiguration apiConfiguration = null;
        try {
            // Mapper with default configuration
            ObjectMapper mapper = new YAMLMapper();
            apiConfiguration = mapper.readValue(yamlSource, ApiConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiConfiguration;
    }

    private File mapApiConfigurationToYamlFile(ApiConfiguration apiConfiguration) {
        File tmpFile = null;
        try {
            // Mapper with default configuration
            ObjectMapper mapper = new YAMLMapper();
            tmpFile = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
            // Write object as YAML file
            mapper.writeValue(tmpFile, apiConfiguration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpFile;
    }
}
