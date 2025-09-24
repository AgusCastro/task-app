package com.challenge.taskapp.exception;

public class StatusUpdateException extends RuntimeException {

    public StatusUpdateException() {
        super("Invalid status change");
    }

    public StatusUpdateException(String message) {
        super(message);
    }
}
