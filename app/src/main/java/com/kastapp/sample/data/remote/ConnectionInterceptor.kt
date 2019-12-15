package com.kastapp.sample.data.remote

import android.content.Context
import com.kastapp.sample.data.NoNetworkException
import com.kastapp.sample.ui.common.ext.isNetworkAvailable
import okhttp3.Interceptor
import okhttp3.Response

class ConnectionInterceptor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!context.isNetworkAvailable()) {
            throw NoNetworkException()
        }
        return chain.proceed(chain.request())
    }
}
