package com.pei.httpmanager;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Response {

    protected int statusCode;
    protected String statusMessage;
    protected Map<String, List<String>> headers;
    protected Request request;
    protected byte[] bytes;
    protected String string;

    private Response(Builder builder) {
        this.statusCode = builder.statusCode;
        this.statusMessage = builder.statusMessage;
        this.headers = builder.headers;
        this.request = builder.request;
        this.bytes = builder.bytes;
        this.string = builder.string;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public byte[] getBytes() {
        if (bytes != null) return bytes;
        if (string != null) {
            bytes = string.getBytes();
        }
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
        this.string = null;
    }

    public String getString() {
        return getString(StandardCharsets.UTF_8);
    }

    public String getString(Charset charset) {
        if (string != null) return string;
        if (bytes != null) {
            string = new String(bytes, charset);
        }
        return string;
    }

    public void setString(String string) {
        this.string = string;
        this.bytes = null;
    }

    public static class Builder {
        int statusCode;
        String statusMessage;
        Map<String, List<String>> headers;
        Request request;
        byte[] bytes;
        String string;

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder statusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            this.headers = headers;
            return this;
        }

        public Builder request(Request request) {
            this.request = request;
            return this;
        }

        public Builder bytes(byte[] bytes) {
            this.bytes = bytes;
            return this;
        }

        public Builder string(String string) {
            this.string = string;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}