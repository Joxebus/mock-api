package io.github.joxebus.mocktester.service;

import static io.github.joxebus.mocktester.common.Constants.NOT_FOUND_CODE;
import static io.github.joxebus.mocktester.common.Constants.OK_CODE;
import static io.github.joxebus.mocktester.common.Constants.YAML_EXT;
import static io.github.joxebus.mocktester.utils.MappingUtils.mapApiConfigurationToEndpointConfiguration;
import static io.github.joxebus.mocktester.utils.MappingUtils.mapYamlFileToApiConfiguration;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.joxebus.mocktester.model.ApiConfiguration;
import io.github.joxebus.mocktester.model.ApiResponse;
import io.github.joxebus.mocktester.model.EndpointConfiguration;
import io.github.joxebus.mocktester.model.FileResponse;
import io.github.joxebus.mocktester.model.ResponseError;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EndpointServiceImpl implements EndpointService {

    private FileService fileService;

    public EndpointServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public ApiResponse getEndpoints() {
        List<String> fileNames = fileService.filesWithExtension(YAML_EXT);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(OK_CODE);
        List<EndpointConfiguration> endpointConfigurations = fileNames.stream().parallel()
                .map(filename -> {
                    FileResponse fileResponse = fileService.download(filename);
                    ApiConfiguration apiConfiguration = mapYamlFileToApiConfiguration(fileResponse.getFile());
                    return mapApiConfigurationToEndpointConfiguration(apiConfiguration);
                }).collect(Collectors.toList());

        apiResponse.setBody(endpointConfigurations);
        return apiResponse;

    }

    @Override
    public ApiResponse getEndpoint(String apiName) {
        ApiResponse apiResponse = new ApiResponse();
        FileResponse fileResponse = fileService.download(apiName+YAML_EXT);
        if(fileResponse.isSuccess()) {
            ApiConfiguration apiConfiguration = mapYamlFileToApiConfiguration(fileResponse.getFile());
            EndpointConfiguration endpointConfiguration = mapApiConfigurationToEndpointConfiguration(apiConfiguration);
            apiResponse.setStatusCode(OK_CODE);
            apiResponse.setBody(endpointConfiguration);
        } else {
            String message = String.format("The endpoint [%s] is not configured.", apiName);
            log.warn(message);
            apiResponse.setStatusCode(NOT_FOUND_CODE);
            apiResponse.setBody(ResponseError.newError(message));
        }
        return apiResponse;
    }
}
