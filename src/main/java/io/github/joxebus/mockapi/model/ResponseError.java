package io.github.joxebus.mockapi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public final class ResponseError {
    private String message;

    private ResponseError(String message) {
        this.message = message;
    }

    public static ResponseError newError(String errorMessage) {
        return new ResponseError(errorMessage);
    }
}
