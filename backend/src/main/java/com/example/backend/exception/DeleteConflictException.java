package com.example.backend.exception;

public class DeleteConflictException extends RuntimeException {
    public DeleteConflictException(String message) {
        super(message);
    }

    public DeleteConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

