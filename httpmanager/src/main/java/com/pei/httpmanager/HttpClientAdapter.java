package com.pei.httpmanager;

import androidx.annotation.WorkerThread;

public interface HttpClientAdapter {

    void send(Request request, ResponseCallback<Response> callback);

    @WorkerThread
    Response send(Request request);
}
