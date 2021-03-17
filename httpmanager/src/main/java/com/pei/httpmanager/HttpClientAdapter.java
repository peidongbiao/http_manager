package com.pei.httpmanager;

public interface HttpClientAdapter {

    void send(Request request, ResponseCallback<Response> callback);
}


