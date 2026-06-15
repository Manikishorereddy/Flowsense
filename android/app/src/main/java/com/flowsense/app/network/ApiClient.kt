package com.flowsense.app.network

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object ApiClient {
    // 10.0.2.2 is the special alias to your host loopback interface (localhost) on the Android emulator
    const val BASE_URL = "http://10.216.113.109/flowsense backend/api/"

    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}
