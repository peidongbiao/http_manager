package com.pei.httpmanager.requestbody;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import okio.BufferedSink;
import okio.Okio;

public class TextRequestBody extends RequestBody {
    private static final String CONTENT_TYPE = "text/plain";
    private String text;

    public TextRequestBody(String text) {
        this(CONTENT_TYPE, text);
    }

    public TextRequestBody(String contentType, String text) {
        super(contentType);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        BufferedSink sink = Okio.buffer(Okio.sink(outputStream));
        sink.writeString(text, StandardCharsets.UTF_8);
        sink.flush();
    }
}
