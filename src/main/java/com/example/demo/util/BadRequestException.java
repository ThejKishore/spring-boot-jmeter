package com.example.demo.util;

/**
 * 400 Bad Request exception.
 */
public final class BadRequestException extends HttpException {
    /**
     * Creates a BadRequestException with the given message.
     *
     * @param message description of the client error
     */
    public BadRequestException(String message) {
        super(message, 400);
    }
}
