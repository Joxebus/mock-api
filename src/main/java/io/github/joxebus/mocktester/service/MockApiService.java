package io.github.joxebus.mocktester.service;

import static io.github.joxebus.mocktester.common.Constants.NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import io.github.joxebus.mocktester.model.ApiConfiguration;
import io.github.joxebus.mocktester.model.ApiResponse;
import io.github.joxebus.mocktester.model.FileResponse;
import io.github.joxebus.mocktester.model.Operation;
import io.github.joxebus.mocktester.service.common.FileService;

@Service
public class MockApiService {

    private FileService fileService;

    public MockApiService(FileService fileService) {
        this.fileService = fileService;
    }

    public FileResponse createOrUpdate(ApiConfiguration apiConfiguration) {
        File configuration = mapApiConfigurationToYamlFile(apiConfiguration);
        return fileService.upload(apiConfiguration.getName(), configuration);
    }

    public ApiResponse getOperation(String uri, String method) {
        String[] uriParts = uri.substring(1).split("/");
        // TODO perform validations
        String fileName = uriParts[0];
        String operationName = uriParts[1];
        FileResponse fileResponse = fileService.download(fileName);
        ApiResponse apiResponse = null;
        if(fileResponse.isSuccess()) {
            ApiConfiguration apiConfiguration = mapYamlFileToApiConfiguration(fileResponse.getFile());
            apiResponse = apiConfigurationToApiResponse(apiConfiguration, operationName, method);
        } else {
            apiResponse = new ApiResponse();
            apiResponse.setStatusCode(NOT_FOUND);
        }
        return apiResponse;
    }

    private ApiResponse apiConfigurationToApiResponse(ApiConfiguration apiConfiguration,
                                                      String operationName, String method) {
        ApiResponse apiResponse = new ApiResponse();

        if(Objects.nonNull(apiConfiguration) && apiConfiguration.getOperations().containsKey(operationName)) {
            Operation operation = apiConfiguration.getOperations().get(operationName);
            apiResponse.setStatusCode(operation.getStatusCode());
            apiResponse.setBody(operation.getBody());
        } else {
            apiResponse.setStatusCode(NOT_FOUND);
        }

        return apiResponse;
    }

    public ApiConfiguration getConfiguration(String apiName) {
        ApiConfiguration apiConfiguration = null;
        FileResponse fileResponse = fileService.download(apiName);
        if(fileResponse.isSuccess()) {
            apiConfiguration = mapYamlFileToApiConfiguration(fileResponse.getFile());
        }
        return apiConfiguration;
    }

    // Helper Methods

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
