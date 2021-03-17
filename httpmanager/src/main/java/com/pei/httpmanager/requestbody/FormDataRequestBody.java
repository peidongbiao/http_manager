package com.pei.httpmanager.requestbody;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class FormDataRequestBody extends RequestBody {
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    Map<String, String> formData;

    public FormDataRequestBody(Map<String, String> formData) {
        super(CONTENT_TYPE);
        this.formData = formData;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {

    }

    public Map<String, String> getFormData() {
        return formData;
    }
}
