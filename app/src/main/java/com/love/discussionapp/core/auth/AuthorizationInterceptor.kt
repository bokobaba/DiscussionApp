package com.love.discussionapp.core.auth

import android.util.Log
import com.love.discussionapp.core.auth.IAuth
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    private val auth: IAuth
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${auth.accessToken ?: ""}")
            .build()

        return chain.proceed(request)
    }
}
