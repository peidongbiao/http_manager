package com.pei.httpmanager.requestbody;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class FileRequestBody extends RequestBody {
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    String fileName;
    File file;

    public FileRequestBody(String fileName, File file) {
        this(DEFAULT_CONTENT_TYPE, fileName, file);
    }

    public FileRequestBody(String contentType, String fileName, File file) {
        super(contentType);
        this.fileName = fileName;
        this.file = file;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        Source source = Okio.buffer(Okio.source(file));
        BufferedSink bufferedSink = Okio.buffer(Okio.sink(outputStream));
        bufferedSink.writeAll(source);
        source.close();
    }

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }
}
