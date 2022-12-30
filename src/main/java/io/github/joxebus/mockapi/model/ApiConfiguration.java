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
    private boolean secured;
    private String authConfig;
    private Map<String, ApiOperation> operations = new HashMap<>();

    public ApiOperation findOperation(String operationName) {
        return operations.get(operationName);
    }
}
