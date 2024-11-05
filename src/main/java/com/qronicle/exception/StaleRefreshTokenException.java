package com.qronicle.exception;

public class StaleRefreshTokenException extends RuntimeException {
    public StaleRefreshTokenException(String message) {
        super(message);
    }
}
