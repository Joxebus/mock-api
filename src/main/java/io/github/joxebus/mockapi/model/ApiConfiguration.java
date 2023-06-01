package io.github.joxebus.mockapi.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiConfiguration {
    private String name;
    private String description;
    private String termsOfService; // URL
    private String version;
    private ApiContact contact;
    private ApiLicense license;
    private boolean secured;
    private String authConfig;
    private Map<String, ApiPath> paths = new HashMap<>();

    public ApiPath findPath(String operationName) {
        return paths.get(operationName);
    }
}
