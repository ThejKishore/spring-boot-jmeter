package com.example.demo.util;


/**
 * Base HTTP exception carrying an HTTP status code alongside a message.
 *
 * <p>This sealed hierarchy centralizes common behavior for application-specific
 * HTTP errors and is mapped to responses by the functional router error handlers.</p>
 */
public sealed class HttpException extends RuntimeException
        permits ResourceNotFoundException, BadRequestException, UnauthorizedException, ForbiddenException, InternalServerErrorException {

    private final int status;

    /**
     * Creates a new HttpException.
     *
     * @param message human-readable error message
     * @param status  HTTP status code to return
     */
    public HttpException(String message, int status) {
        super(message);
        this.status = status;
    }

    /**
     * Gets the HTTP status code associated with this exception.
     *
     * @return the HTTP status code
     */
    public int getStatus() {
        return status;
    }
}