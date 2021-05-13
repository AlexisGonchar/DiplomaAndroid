package com.alejostudio.practicemobile;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class OkHttpSender {

    static OkHttpClient client = new OkHttpClient();

    public static void send(String url, String json, Callback callback) {

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(RequestBody
                        .create(
                                MediaType.parse("application/json"),json
                        )
                )
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void send(String url, Callback callback) {

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

}
