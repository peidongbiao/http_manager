package com.pei.http_manager;

import com.pei.httpmanager.HttpClientAdapter;
import com.pei.httpmanager.Request;
import com.pei.httpmanager.Response;
import com.pei.httpmanager.ResponseCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import okio.Okio;

public class HttpUrlConnectionClientAdapter implements HttpClientAdapter {

    private ExecutorService mExecutorService;

    public HttpUrlConnectionClientAdapter() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    @Override
    public void send(final Request request, final ResponseCallback callback) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = request(request);
                    callback.onSuccess(response);
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError(e);
                }
            }
        });
    }

    private Response request(Request request) throws IOException {
        URL url = new URL(request.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(request.getMethod().getValue().toUpperCase());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        //request headers
        if (request.getHeaders() != null) {
            for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
                List<String> values = entry.getValue();
                if (values != null) {
                    for (int i = 0; i < values.size(); i++) {
                        connection.addRequestProperty(entry.getKey(), values.get(i));
                    }
                }
            }
        }

        connection.connect();

        if (request.getRequestBody() != null) {
            request.getRequestBody().write(connection.getOutputStream());
        }

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
            Response.Builder builder = new Response.Builder();
            builder.statusCode(responseCode);
            builder.statusMessage(responseMessage);
            builder.headers(connection.getHeaderFields());
            builder.bytes(getResponseByte(connection.getInputStream()));
            builder.request(request);
            return builder.build();
        } else {
            throw new IOException("request failed code: " + responseCode + ", msg: " + responseMessage);
        }
    }

    private byte[] getResponseByte(InputStream inputStream) throws IOException {
        return Okio.buffer(Okio.source(inputStream)).readByteArray();
    }
}