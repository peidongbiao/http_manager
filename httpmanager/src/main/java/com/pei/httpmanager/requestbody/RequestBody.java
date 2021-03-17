package com.pei.httpmanager.requestbody;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;
import okio.Source;


public abstract class RequestBody {

    private String contentType;

    public static RequestBody formData(Map<String, String> formData) {
        return new FormDataRequestBody(formData);
    }

    public static RequestBody create(String contentType, String content) {
        return new TextRequestBody(contentType, content);
    }

    public RequestBody(String contentType) {
        this.contentType = contentType;
    }


    public String getContentType() {
        return contentType;
    }

    public abstract void write(OutputStream outputStream) throws IOException;
}

