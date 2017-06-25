package com.ilaoda.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by hbh on 2017/6/25.
 * 像服务器发送请求需求
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {

        // 1. 创建client对象
        OkHttpClient client = new OkHttpClient();
        // 2. 过去请求对象
        Request request = new Request.Builder().url(address).build();

        // 3. client发送请求
        client.newCall(request).enqueue(callback);
    }
}
