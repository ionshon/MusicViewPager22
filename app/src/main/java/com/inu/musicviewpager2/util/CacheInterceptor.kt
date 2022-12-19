package com.inu.musicviewpager2.util

import android.content.Context
import android.net.ConnectivityManager
//import android.net.NetworkInfo
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class CacheInterceptor(private val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        if (request.header("Cache-control") == null) {
            // 캐시 컨트롤 따로 설정 안 해두었다면 캐시 설정
            request = if (hasNetwork(context)) {
                request
                    .newBuilder()
                    .header("Cache-control", "public, max-age=${60 * 60 * 24 * 5}") // 5초
                    .addHeader("Connection", "close")
                    .build()
            } else {
                request
                    .newBuilder()
                    .header("Cache-control", "public, only-if-cached, max-stale=${60 * 60 * 24 * 7}") // 일주일
                    .build()
            }
        }
        return chain.proceed(request)
    }

    private fun hasNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return connectivityManager.isDefaultNetworkActive //activeNetwork != null && activeNetwork.isConnected
    }
}