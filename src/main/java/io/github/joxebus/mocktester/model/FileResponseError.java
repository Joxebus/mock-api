package io.github.joxebus.mocktester.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public final class FileResponseError {
    private String message;

    private FileResponseError(String message) {
        this.message = message;
    }

    public static FileResponseError newError(String errorMessage) {
        return new FileResponseError(errorMessage);
    }
}
