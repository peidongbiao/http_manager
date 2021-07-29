package com.pei.http_manager;

import androidx.annotation.NonNull;

import com.pei.httpmanager.HttpClientAdapter;
import com.pei.httpmanager.Request;
import com.pei.httpmanager.Response;
import com.pei.httpmanager.ResponseCallback;
import com.pei.httpmanager.requestbody.FileRequestBody;
import com.pei.httpmanager.requestbody.FormDataRequestBody;
import com.pei.httpmanager.requestbody.MultipartRequestBody;
import com.pei.httpmanager.requestbody.TextRequestBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.Okio;

public class OkHttpClientAdapter implements HttpClientAdapter {

    private OkHttpClient okHttpClient;

    public OkHttpClientAdapter(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public void send(Request request, ResponseCallback<Response> callback) {
        okhttp3.Request okhttpRequest = makeRequest(request);
        Call call = okHttpClient.newCall(okhttpRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(new Exception(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response okhttpResponse) throws IOException {
                Response response = parseResponse(request, okhttpResponse);
                callback.onSuccess(response);
            }
        });
    }

    @Override
    public Response send(Request request) {
        return null;
    }

    private okhttp3.Request makeRequest(Request request) {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.url(request.getUrl());

        //header
        if (request.getHeaders() != null && request.getHeaders().size() > 0) {
            for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
                String name = entry.getKey();
                for (int i = 0; i < entry.getValue().size(); i++) {
                    builder.addHeader(name, entry.getValue().get(i));
                }
            }
        }

        builder.method(request.getMethod().getValue(), parseRequestBody(request));

        return builder.build();
    }

    private okhttp3.RequestBody parseRequestBody(Request request) {
        if (request.getRequestBody() == null) {
            return null;
        }
        if (request.getRequestBody() instanceof TextRequestBody) {
            TextRequestBody textRequestBody = (TextRequestBody) request.getRequestBody();
            return okhttp3.RequestBody.create(MediaType.parse(textRequestBody.getContentType()), textRequestBody.getText());
        } else if (request.getRequestBody() instanceof FormDataRequestBody) {
            FormDataRequestBody formDataRequestBody = (FormDataRequestBody) request.getRequestBody();
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (formDataRequestBody.getFormData() != null) {
                for (Map.Entry<String, String> entry : formDataRequestBody.getFormData().entrySet()) {
                    formBodyBuilder.add(entry.getKey(), entry.getValue());
                }
            }
            return formBodyBuilder.build();
        } else if (request.getRequestBody() instanceof MultipartRequestBody) {
            MultipartRequestBody multipartRequestBody = (MultipartRequestBody) request.getRequestBody();
            List<MultipartRequestBody.Part> parts = multipartRequestBody.getParts();
            if (parts == null || parts.size() == 0) {
                return null;
            }
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
            for (int i = 0; i < parts.size(); i++) {
                MultipartRequestBody.Part part = parts.get(i);
                if (part.getBody() instanceof TextRequestBody) {
                    multipartBodyBuilder.addFormDataPart(part.getName(), ((TextRequestBody) part.getBody()).getText());
                } else if (part.getBody() instanceof FileRequestBody) {
                    FileRequestBody fileRequestBody = (FileRequestBody) part.getBody();
                    multipartBodyBuilder.addFormDataPart(part.getName(), fileRequestBody.getFileName(), RequestBody.create(MediaType.parse(fileRequestBody.getContentType()), fileRequestBody.getFile()));
                }
            }
            return multipartBodyBuilder.build();
        } else {
            return null;
        }
    }

    private Response parseResponse(Request request, okhttp3.Response okhttpResponse) throws IOException {
        Response.Builder builder = new Response.Builder();
        Response response = builder.request(request)
                .statusCode(okhttpResponse.code())
                .statusMessage(okhttpResponse.message())
                .headers(okhttpResponse.headers().toMultimap())
                .bytes(Okio.buffer(Okio.source(okhttpResponse.body().byteStream())).readByteArray())
                .build();
        okhttpResponse.close();
        return response;
    }
}