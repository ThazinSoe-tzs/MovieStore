package com.thazin.moviestore.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.thazin.moviestore.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            logging.setLevel((HttpLoggingInterceptor.Level.BODY))
            val client = OkHttpClient.Builder().addInterceptor(logging).build()
            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(client).build()
        }

        val api: ApiServices by lazy {
            retrofit.create(ApiServices::class.java)
        }
    }
}