package com.thazin.moviestore.api

import com.thazin.moviestore.models.PopularMoviesResponse
import com.thazin.moviestore.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("/3/movie/popular")
    suspend fun getPopularMoviesAsync(@Query("api_key") api_key: String = API_KEY): Response<PopularMoviesResponse>

    @GET("/3/movie/upcoming")
    suspend fun getLatestMoviesAsync(@Query("api_key") api_key: String = API_KEY): Response<PopularMoviesResponse>
}