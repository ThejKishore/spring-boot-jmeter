package com.example.demo.util;

/**
 * 500 Internal Server Error exception.
 */
public final class InternalServerErrorException extends HttpException {
    /**
     * Creates an InternalServerErrorException with the given message.
     *
     * @param message description of the server error
     */
    public InternalServerErrorException(String message) {
        super(message, 500);
    }
}
