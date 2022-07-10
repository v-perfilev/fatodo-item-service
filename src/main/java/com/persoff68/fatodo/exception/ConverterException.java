package com.persoff68.fatodo.exception;

import org.springframework.http.HttpStatus;

public class ConverterException extends AbstractException {
    public ConverterException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Internal client error");
    }
}
