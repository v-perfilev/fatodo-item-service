package com.persoff68.fatodo.service.exception;

import com.persoff68.fatodo.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class GroupInvalidException extends AbstractException {
    private static final String MESSAGE = "Group is invalid";
    private static final String FEEDBACK_CODE = "service.group.invalid";

    public GroupInvalidException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE, FEEDBACK_CODE);
    }

    public GroupInvalidException(String message) {
        super(HttpStatus.BAD_REQUEST, message, FEEDBACK_CODE);
    }

}
