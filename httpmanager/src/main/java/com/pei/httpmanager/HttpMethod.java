package com.pei.httpmanager;

public enum HttpMethod {

    POST("POST"),
    GET("GET"),
    DELETE("DELETE"),
    PUT("PUT"),
    HEAD("HEAD"),
    PATCH("PATCH"),
    OPTIONS("OPTIONS");

    private String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}