package io.github.joxebus.mockapi.utils;

import static io.github.joxebus.mockapi.common.Constants.METHOD_NOT_ALLOWED;
import static io.github.joxebus.mockapi.common.Constants.NOT_FOUND_CODE;
import static io.github.joxebus.mockapi.common.Constants.PATH_CONFIG;
import static io.github.joxebus.mockapi.common.Constants.PATH_MOCK_API;
import static io.github.joxebus.mockapi.common.Constants.SLASH;
import static io.github.joxebus.mockapi.common.Constants.UNAUTHORIZED;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import io.github.joxebus.mockapi.model.ApiConfiguration;
import io.github.joxebus.mockapi.model.ApiPath;
import io.github.joxebus.mockapi.model.ApiResponse;
import io.github.joxebus.mockapi.model.Endpoint;
import io.github.joxebus.mockapi.model.EndpointConfiguration;
import io.github.joxebus.mockapi.model.ResponseError;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MappingUtils {

    public static EndpointConfiguration mapApiConfigurationToEndpointConfiguration(ApiConfiguration apiConfiguration) {
        String apiName = apiConfiguration.getName();
        String config = PATH_CONFIG + SLASH + apiName;
        List<Endpoint> endpoints = apiConfiguration.getPaths().entrySet().stream()
                .map(entry -> {
                    String href = PATH_MOCK_API + SLASH + apiName + SLASH + entry.getKey();
                    String method = entry.getValue().getMethod().toUpperCase();
                    int statusCode = entry.getValue().getStatusCode();
                    return new Endpoint(href, method, statusCode);
                }).collect(Collectors.toList());
        return new EndpointConfiguration(config, endpoints);
    }

    public static ApiResponse mapApiConfigurationToApiResponse(ApiConfiguration apiConfiguration,
                                                               String path, String method,
                                                               String authorization) {
        ApiResponse apiResponse = new ApiResponse();

        if(Objects.isNull(apiConfiguration) || !apiConfiguration.getPaths().containsKey(path)) {
            String message = String.format("There are no configuration for path [%s]", path);
            log.warn(message);
            apiResponse.setStatusCode(NOT_FOUND_CODE);
            apiResponse.setBody(ResponseError.newError(message));
            return apiResponse;
        }

        ApiPath apiPath = apiConfiguration.findPath(path);

        if(apiConfiguration.isSecured() && !apiConfiguration.getAuthConfig().equalsIgnoreCase(authorization)) {
            String message = String.format("UNAUTHORIZED missing or wrong auth info for [%s]", path);
            log.warn(message);
            apiResponse.setStatusCode(UNAUTHORIZED);
            apiResponse.setBody(ResponseError.newError(message));
            return apiResponse;
        }

        if(method.equalsIgnoreCase(apiPath.getMethod())) {
            apiResponse.setHeaders(apiPath.getHeaders());
            apiResponse.setStatusCode(apiPath.getStatusCode());
            apiResponse.setBody(apiPath.getBody());
        } else {
            String message = String.format("METHOD NOT ALLOWED for path [%s]", path);
            log.warn(message);
            apiResponse.setStatusCode(METHOD_NOT_ALLOWED);
            apiResponse.setBody(ResponseError.newError(message));
        }

        return apiResponse;
    }

    public static ApiConfiguration mapYamlFileToApiConfiguration(File yamlSource) {
        ApiConfiguration apiConfiguration = null;
        try {
            ObjectMapper mapper = new YAMLMapper();
            apiConfiguration = mapper.readValue(yamlSource, ApiConfiguration.class);
        } catch (IOException e) {
            log.error("There was an error while reading File configuration", e);
        }
        return apiConfiguration;
    }

    public static File mapApiConfigurationToYamlFile(ApiConfiguration apiConfiguration) {
        File tmpFile = null;
        try {
            ObjectMapper mapper = new YAMLMapper();
            tmpFile = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
            mapper.writeValue(tmpFile, apiConfiguration);
        } catch (IOException e) {
            log.error("There was an error while mapping ApiConfiguration to File", e);
        }
        return tmpFile;
    }

    public static ResponseEntity<Object> buildResponseWithHeaders(ApiResponse apiResponse) {
        ResponseEntity.BodyBuilder responseEntityBuilder = ResponseEntity.status(apiResponse.getStatusCode());
        apiResponse.getHeaders().forEach(responseEntityBuilder::header);
        return responseEntityBuilder.body(apiResponse.getBody());
    }
}
