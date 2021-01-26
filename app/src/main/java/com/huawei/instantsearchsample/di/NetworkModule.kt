package com.huawei.instantsearchsample.di

import android.content.Context
import com.huawei.instantsearchsample.rest.QueryService
import com.huawei.instantsearchsample.util.ApplicationConstants
import com.huawei.secure.android.common.ssl.SecureSSLSocketFactory
import com.huawei.secure.android.common.ssl.SecureX509TrustManager
import com.huawei.secure.android.common.ssl.hostname.StrictHostnameVerifier
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

val networkModule = module {
    factory { provideOkHttpClient(androidContext()) }
    single { provideRetrofit((get())) }
    single { provideService<QueryService>(get()) }
}


fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(ApplicationConstants.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(context: Context): OkHttpClient {
    val ssf: SSLSocketFactory = SecureSSLSocketFactory.getInstance(context)
    val xtm: X509TrustManager = SecureX509TrustManager(context)

    return OkHttpClient().newBuilder()
        .sslSocketFactory(ssf, xtm)
        .hostnameVerifier(StrictHostnameVerifier())
        .retryOnConnectionFailure(true)
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()
}

inline fun <reified T> provideService(retrofit: Retrofit): T {
    return retrofit.create(T::class.java)
}