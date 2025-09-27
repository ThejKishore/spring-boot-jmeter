package com.example.demo.util;

/**
 * 401 Unauthorized exception.
 */
public final class UnauthorizedException extends HttpException {
    /**
     * Creates an UnauthorizedException with the given message.
     *
     * @param message description of the authentication error
     */
    public UnauthorizedException(String message) {
        super(message, 401);
    }
}
