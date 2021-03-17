package com.pei.http_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pei.httpmanager.HttpManager;
import com.pei.httpmanager.Request;
import com.pei.httpmanager.Response;
import com.pei.httpmanager.ResponseCallback;
import com.pei.httpmanager.exception.ConvertException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static String BASE_URL = "https://jsonplaceholder.typicode.com";
    HttpManager mHttpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpClient client = new OkHttpClient.Builder().build();
        OkHttpClientAdapter okHttpClientAdapter = new OkHttpClientAdapter(client);

        HttpManager.Converter gsonConverter = new HttpManager.Converter() {
            @Override
            public <T> T convert(Response response, Type type) throws ConvertException {
                return new Gson().fromJson(response.getString(), type);
            }
        };
        mHttpManager = new HttpManager.Builder()
                .setHttpClientAdapter(okHttpClientAdapter)
                .setConverter(gsonConverter)
                .setBaseUrl(BASE_URL)
                .build();
    }


    public void sendRequest(View view) {
        Request request = new Request.Builder()
                .baseUrl(BASE_URL)
                .get("/posts")
                .addQueryParam("_start", "0")
                .addQueryParam("_limit", "20")
                .build();

        mHttpManager.send(request, new ResponseCallback<List<Map<String,String>>>(new TypeToken<List<Map<String,String>>>(){}.getType()) {

            @Override
            public void onSuccess(List<Map<String,String>> data) {
                Log.d(TAG, "onSuccess: " + data.toString());
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: ", e);
            }
        });
    }
}