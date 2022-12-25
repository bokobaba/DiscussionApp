package com.love.discussionapp.di

import android.annotation.SuppressLint
import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.love.discussionapp.BuildConfig
import com.love.discussionapp.core.auth.AuthorizationInterceptor
import com.love.discussionapp.core.auth.*
import com.love.discussionapp.core.data.remote.Api
import com.love.discussionapp.core.data.repository.ApiRepository
import com.love.discussionapp.core.data.repository.DataStoreRepository
import com.love.discussionapp.core.data.repository.IDataStoreRepository
import com.love.discussionapp.core.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuth(): IAuth {
        return Auth()
    }

    @Provides
    @Singleton
    fun provideClient(authorizationInterceptor: AuthorizationInterceptor): ApolloClient {
        val client = OkHttpClient.Builder()
            .addInterceptor(authorizationInterceptor)

        if (BuildConfig.DEBUG) {
            //set self sign certificate
            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            client
                .sslSocketFactory(
                    sslSocketFactory,
                    trustAllCerts[0] as X509TrustManager
                )
                .hostnameVerifier { _, _ -> true }
        }

//            .build()

        return ApolloClient.Builder()
            .serverUrl(Constants.BASE_URL)
            .okHttpClient(client.build())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(auth: IAuth): AuthorizationInterceptor {
        return AuthorizationInterceptor(auth)
    }

    @Provides
    @Singleton
    fun provideApi(apolloClient: ApolloClient): Api {
        return Api(apolloClient)
    }

    @Provides
    @Singleton
    fun provideApiRepository(api: Api): ApiRepository {
        return ApiRepository(api)
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext context: Context): IDataStoreRepository {
        return DataStoreRepository(context)
    }
}