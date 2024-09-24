package com.example.voicechanger.di

import android.content.Context
import com.example.voicechanger.network.ApiInterface
import com.example.voicechanger.network.NetworkInterceptor
import com.example.voicechanger.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Provides
    @Singleton
    fun provideHttpClient(cache: Cache?): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .cache(cache)
        return okHttpClientBuilder
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val requestBuilder = chain.request()
                    .newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                chain.proceed(requestBuilder.build())
            })

            .addInterceptor(NetworkInterceptor())
            .connectTimeout(Constants.DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(Constants.DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Constants.DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext application: Context): Cache {
        val cacheSize = 10 * 1024 * 1024.toLong() // 10 MB
        val httpCacheDirectory = File(application.cacheDir, "http-cache")
        return Cache(httpCacheDirectory, cacheSize)
    }

    @Provides
    @Singleton
    fun provideApiInterface(gson: Gson, client: OkHttpClient)
            : ApiInterface {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.ApiComponents.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(ApiInterface::class.java)
    }
}