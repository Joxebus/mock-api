package io.github.joxebus.mocktester.model;

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
public class Operation {

    private String method;
    private Map<String, String[]> headers;
    private int statusCode;
    private String body;

}
