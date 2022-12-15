package io.github.joxebus.mockapi.service;

import static io.github.joxebus.mockapi.common.Constants.NOT_FOUND_CODE;
import static io.github.joxebus.mockapi.common.Constants.PATH_MOCK_API_LENGTH;
import static io.github.joxebus.mockapi.common.Constants.SLASH;
import static io.github.joxebus.mockapi.common.Constants.YAML_EXT;
import static io.github.joxebus.mockapi.utils.MappingUtils.mapApiConfigurationToApiResponse;
import static io.github.joxebus.mockapi.utils.MappingUtils.mapYamlFileToApiConfiguration;

import org.springframework.stereotype.Service;

import io.github.joxebus.mockapi.model.ApiConfiguration;
import io.github.joxebus.mockapi.model.ApiResponse;
import io.github.joxebus.mockapi.model.FileResponse;
import io.github.joxebus.mockapi.model.ResponseError;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MockApiServiceImpl implements MockApiService {

    private FileService fileService;

    public MockApiServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public ApiResponse getMockApiOperation(String uri, String method) {
        String[] uriParts = uri.substring(PATH_MOCK_API_LENGTH+1).split(SLASH);
        String fileName = uriParts[0];
        String operationName = uriParts[1];
        FileResponse fileResponse = fileService.download(fileName+YAML_EXT);
        ApiResponse apiResponse = null;
        if(fileResponse.isSuccess()) {
            ApiConfiguration apiConfiguration = mapYamlFileToApiConfiguration(fileResponse.getFile());
            apiResponse = mapApiConfigurationToApiResponse(apiConfiguration, operationName, method);
        } else {
            String message = String.format("Configuration not found for URI [%s]", uri);
            log.warn(message);
            apiResponse = new ApiResponse();
            apiResponse.setStatusCode(NOT_FOUND_CODE);
            apiResponse.setBody(ResponseError.newError(message));
        }
        return apiResponse;
    }

}
