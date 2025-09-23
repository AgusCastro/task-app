package com.challenge.taskapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class MissingTenantIdException extends RuntimeException {
    public MissingTenantIdException() {
        super("Tenant ID header is missing from the request.");
    }
}
