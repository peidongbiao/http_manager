package com.pei.httpmanager;

import android.net.Uri;
import android.text.TextUtils;

import com.pei.httpmanager.requestbody.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class Request {

    HttpMethod method;
    Map<String, List<String>> headers;
    Map<String, String> queryParams;
    /**
     * request base url
     * 默认为HttpManager的baseUrl
     */
    String baseUrl;
    String path;
    RequestBody requestBody;
    /**
     * 回调执行的线程
     * @see CallbackExecutor
     */
    Executor callbackExecutor;

    private Request(Builder builder) {
        this.method = builder.method;
        this.headers = builder.headers;
        this.queryParams = builder.queryParams;
        this.baseUrl = builder.baseUrl;
        this.path = builder.path;
        this.requestBody = builder.requestBody;
        this.callbackExecutor = builder.callbackExecutor;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        List<String> list = headers.get(key);
        if (list == null) {
            list = new ArrayList<>();
            headers.put(key, list);
        }
        list.add(value);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }


    public Executor getCallbackExecutor() {
        return callbackExecutor;
    }

    public void setCallbackExecutor(Executor callbackExecutor) {
        this.callbackExecutor = callbackExecutor;
    }

    public String getUrl() {
        String url;
        if (path.startsWith("http")) {
            url = path;
        } else {
            url = baseUrl + path;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.appendQueryParameter(Uri.encode(entry.getKey()), Uri.encode(entry.getValue()));
            }
        }
        return builder.build().toString();
    }

    public static class Builder {
        HttpMethod method;
        String baseUrl;
        String path;
        Map<String, List<String>> headers;
        Map<String, String> queryParams;
        RequestBody requestBody;
        Executor callbackExecutor;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder path(String url) {
            this.path = url;
            return this;
        }

        public Builder addHeader(String key, String value) {
            if (headers == null) {
                headers = new HashMap<>();
            }
            List<String> list = headers.get(key);
            if (list == null) {
                list = new ArrayList<>();
                headers.put(key, list);
            }
            list.add(value);
            return this;
        }

        public Builder addQueryParam(String key, String value) {
            if (queryParams == null) {
                queryParams = new HashMap<>();
            }
            queryParams.put(key, value);
            return this;
        }

        public Builder get(String path) {
            this.path = path;
            return method(HttpMethod.GET, null);
        }

        public Builder post(String path, RequestBody body) {
            this.path = path;
            return method(HttpMethod.POST, body);
        }

        public Builder method(HttpMethod method, RequestBody body) {
            this.method = method;
            this.requestBody = body;
            return this;
        }

        public Builder callbackExecutor(Executor callbackExecutor) {
            this.callbackExecutor = callbackExecutor;
            return this;
        }

        public Request build() {
            if (TextUtils.isEmpty(path)) throw new IllegalArgumentException("path can't be empty");
            if (method == null) throw new IllegalArgumentException("method can't be null!");
            if (headers == null) {
                headers = new HashMap<>();
            }
            if (queryParams == null) {
                queryParams = new HashMap<>();
            }
            return new Request(this);
        }
    }
}