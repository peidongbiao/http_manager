package com.pei.httpmanager.requestbody;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class MultipartRequestBody extends RequestBody {
    private static final String DEFAULT_CONTENT_TYPE = "multipart/form-data";
    private List<Part> parts;

    public MultipartRequestBody() {
        super(DEFAULT_CONTENT_TYPE);
    }

    public MultipartRequestBody(String contentType) {
        super(contentType);
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {

    }

    public void addPart(Part part) {
        this.parts.add(part);
    }

    public void addText(String name, String contentType, String text) {
        this.parts.add(new Part(name, new TextRequestBody(contentType, text)));
    }

    public void addFile(String name, String contentType, String filename, File file) {
        this.parts.add(new Part(name, new FileRequestBody(contentType, filename, file)));
    }

    public List<Part> getParts() {
        return parts;
    }

    public static class Part {
        String name;
        Map<String, List<String>> headers;
        RequestBody body;

        public Part(String name, RequestBody body) {
            this(name, null, body);
        }

        public Part(String name, Map<String, List<String>> headers, RequestBody body) {
            this.name = name;
            this.headers = headers;
            this.body = body;
        }

        public String getName() {
            return name;
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public RequestBody getBody() {
            return body;
        }
    }
}
