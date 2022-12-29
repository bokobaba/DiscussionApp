package com.love.discussionapp.core.auth

import android.util.Log
import com.google.gson.Gson
import com.love.discussionapp.core.util.TAG
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    private val auth: IAuth
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d(this.TAG(), "intercepted")
        return if (auth.user != null) {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${auth.accessToken ?: ""}")
                .build()

            chain.proceed(request)
        } else {
            chain.proceed(chain.request())
        }
    }
}
