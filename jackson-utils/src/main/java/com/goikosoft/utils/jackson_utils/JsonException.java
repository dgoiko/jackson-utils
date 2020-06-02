package com.goikosoft.utils.jackson_utils;

public class JsonException extends Exception {
    private static final long serialVersionUID = -877544845077121498L;

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    public String getMessage() {
        return getOnlyMsg() + (this.getCause() != null ? ". Cause: "+getCause() : "");
    }

    protected String getOnlyMsg() {
        return super.getMessage();
    }
}
