package io.github.joxebus.mocktester.service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import io.github.joxebus.mocktester.model.ApiConfiguration;
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

    public Operation getOperation(String uri, String method) {
        String[] uriParts = uri.substring(1).split("/");
        // TODO perform validations
        String fileName = uriParts[0];
        String operationName = uriParts[1];
        FileResponse fileResponse = fileService.download(fileName);
        Operation operation = null;
        if(fileResponse.isSuccess()) {
            ApiConfiguration apiConfiguration = mapYamlFileToApiConfiguration(fileResponse.getFile());
            if(Objects.nonNull(apiConfiguration)) {
                operation = apiConfiguration.getOperations().get(operationName);
            }
        }
        return operation;
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
