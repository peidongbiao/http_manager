package com.pei.httpmanager;


import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;

import com.pei.httpmanager.exception.ConvertException;
import com.pei.httpmanager.exception.HttpRequestException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;


public class HttpManager {

    private String baseUrl;
    private HttpClientAdapter httpClientAdapter;
    private List<Interceptor> interceptors;
    private Executor callbackExecutor;
    private Converter converter;

    private HttpManager(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.httpClientAdapter = builder.httpClientAdapter;
        this.interceptors = Collections.unmodifiableList(builder.interceptors);
        this.callbackExecutor = builder.callbackExecutor;
        this.converter = new DecoratedConverter(builder.converter);
    }

    public <T> void send(Request request, final ResponseCallback<T> callback) {
        if (request.baseUrl == null) {
            request.baseUrl = baseUrl;
        }

        if (request.callbackExecutor == null) {
            request.callbackExecutor = callbackExecutor;
        }

        try {
            for (int i = 0; i < interceptors.size(); i++) {
                request = interceptors.get(i).onRequest(request);
            }
        } catch (Exception e) {
            callOnErrorOnExecutor(callbackExecutor, callback, e);
        }

        final Executor callbackExecutor = request.callbackExecutor;
        httpClientAdapter.send(request, new ResponseCallback<Response>(null) {
            @Override
            public void onSuccess(Response response) {
                try {
                    T data;
                    for (int i = 0; i < interceptors.size(); i++) {
                        response = interceptors.get(i).onResponse(response);
                    }
                    data = converter.convert(response, callback.type);
                    callSuccessOnExecutor(callbackExecutor, callback, data);
                } catch (Exception e) {
                    callOnErrorOnExecutor(callbackExecutor, callback, e);
                }
            }

            @Override
            public void onError(Exception e) {
                callOnErrorOnExecutor(callbackExecutor, callback, e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @WorkerThread
    public <T> T sendSync(Request request, Type type) throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("sendSync method must be called from worker thread!");
        }
        final Object[] result = new Object[1];
        final Exception[] exception = new Exception[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        send(request, new ResponseCallback<T>(type) {
            @Override
            public void onSuccess(T data) {
                result[0] = data;
                countDownLatch.countDown();
            }

            @Override
            public void onError(Exception e) {
                exception[0] = e;
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
        if (result[0] != null) return (T) result[0];
        if (exception[0] != null) throw exception[0];
        throw new IllegalStateException();
    }

    private <T> void callSuccessOnExecutor(Executor executor, final ResponseCallback<T> callback, final T data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(data);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private void callOnErrorOnExecutor(Executor executor, final ResponseCallback callback, final Exception exception) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onError(new HttpRequestException(exception));
            }
        });
    }

    public static class Builder {
        private String baseUrl;
        private HttpClientAdapter httpClientAdapter;
        private List<Interceptor> interceptors;
        private Executor callbackExecutor;
        private Converter converter;

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setHttpClientAdapter(HttpClientAdapter httpClientAdapter) {
            this.httpClientAdapter = httpClientAdapter;
            return this;
        }

        public Builder addInterceptors(Interceptor interceptor) {
            if (this.interceptors == null) {
                this.interceptors = new ArrayList<>();
            }
            this.interceptors.add(interceptor);
            return this;
        }

        public Builder setCallbackExecutor(Executor callbackExecutor) {
            this.callbackExecutor = callbackExecutor;
            return this;
        }

        public Builder setConverter(Converter converter) {
            this.converter = converter;
            return this;
        }

        public HttpManager build() {
            if (httpClientAdapter == null) {
                throw new NullPointerException("httpClientAdapter can't be null");
            }
            if (interceptors == null) {
                interceptors = new ArrayList<>();
            }
            if (callbackExecutor == null) {
                callbackExecutor = CallbackExecutor.main();
            }
            return new HttpManager(this);
        }
    }

    public interface Interceptor {

        @MainThread
        Request onRequest(Request request) throws Exception;

        @WorkerThread
        Response onResponse(Response response) throws Exception;
    }

    public interface Converter {
        <T> T convert(Response response, Type type) throws ConvertException;
    }

    private static class DecoratedConverter implements Converter {
        Converter delegate;

        public DecoratedConverter(Converter delegate) {
            this.delegate = delegate;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T convert(Response response, Type type) throws ConvertException {
            T data;
            try {
                if (type == Response.class) {
                    data = (T) response;
                } else if (type == String.class) {
                    data = (T) response.getString();
                } else if (type == JSONObject.class) {
                    data = (T) new JSONObject(response.getString());
                } else if (type == JSONArray.class) {
                    data = (T) new JSONArray(response.getString());
                } else {
                    data = delegate.convert(response, type);
                }
            } catch (Exception e) {
                throw new ConvertException(e);
            }
            return data;
        }
    }
}