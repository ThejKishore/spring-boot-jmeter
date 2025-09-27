package com.example.demo.util;

/**
 * 403 Forbidden exception.
 */
public final class ForbiddenException extends HttpException {
    /**
     * Creates a ForbiddenException with the given message.
     *
     * @param message description of the authorization error
     */
    public ForbiddenException(String message) {
        super(message, 403);
    }
}
