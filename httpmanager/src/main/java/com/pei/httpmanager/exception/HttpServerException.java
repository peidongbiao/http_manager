package com.pei.httpmanager.exception;

public class HttpServerException extends HttpRequestException {

    int errorCode;

    public HttpServerException(int errorCode) {
        this.errorCode = errorCode;
    }

    public HttpServerException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public HttpServerException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}