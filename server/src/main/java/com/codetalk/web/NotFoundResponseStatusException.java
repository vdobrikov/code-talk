package com.codetalk.web;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class NotFoundResponseStatusException extends ResponseStatusException {
    public NotFoundResponseStatusException() {
        super(NOT_FOUND);
    }

    public NotFoundResponseStatusException(String reason) {
        super(NOT_FOUND, reason);
    }

    public NotFoundResponseStatusException(String reason, Throwable cause) {
        super(NOT_FOUND, reason, cause);
    }
}
