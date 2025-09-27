package com.example.demo.util;

/**
 * 404 Resource Not Found exception.
 */
public final class ResourceNotFoundException extends HttpException {
    /**
     * Creates a ResourceNotFoundException with the given message.
     *
     * @param message description of the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}
