package com.qronicle.exception;

import java.util.Map;

// A class for collecting invalid field values on forms for creating objects (e.g. users)
public class ValidationErrorResponse {
    private int status;
    private Map<String, String> errors;
    private long timestamp;

    public ValidationErrorResponse() {
    }

    public ValidationErrorResponse(int status, Map<String, String> errors, long timestamp) {
        this.status = status;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
