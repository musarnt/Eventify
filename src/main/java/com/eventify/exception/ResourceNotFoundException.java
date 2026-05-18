package com.eventify.exception;

// Domain-level exception thrown when a requested entity does not exist.
// The GlobalExceptionHandler translates it into HTTP 404.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with id " + id + " was not found");
    }
}