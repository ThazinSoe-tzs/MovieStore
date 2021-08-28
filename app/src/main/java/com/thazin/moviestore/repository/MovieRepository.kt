package com.thazin.moviestore.repository

import android.util.Log
import com.thazin.moviestore.api.RetrofitInstance
import com.thazin.moviestore.application.MovieStoreApp
import com.thazin.moviestore.db.MovieDatabase
import com.thazin.moviestore.models.ResultDatabaseModel
import com.thazin.moviestore.models.FavouriteResultDatabaseModel
import com.thazin.moviestore.models.asDataBaseModel
import com.thazin.moviestore.utils.Resource
import com.thazin.moviestore.utils.Status

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieRepository(private val db: MovieDatabase) {

    suspend fun refreshPopularMovieList(): Resource<List<ResultDatabaseModel>> {

        return try {
            val response = RetrofitInstance.api.getPopularMoviesAsync()
            if (response.isSuccessful) {
              val result=  MovieStoreApp.getInstance().getResponseHandler()
                    .handleSuccess(response.body()!!.results.asDataBaseModel(popularMovies = true),
                        response.code())
                handleResult(result)
               return result
            } else {
                MovieStoreApp.getInstance().getResponseHandler()
                    .handleException(response.errorBody()!!.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MovieStoreApp.getInstance().getResponseHandler().handleException(e)
        }

    }

    suspend fun refreshLatestMovieList(): Resource<List<ResultDatabaseModel>>  {
        val response = RetrofitInstance.api.getLatestMoviesAsync()
        return try {
            if (response.isSuccessful) {
                val result = MovieStoreApp.getInstance().getResponseHandler()
                    .handleSuccess(response.body()!!.results.asDataBaseModel(latestMovies = true),
                        response.code())
                handleResult(result)
                return result
            } else {
                MovieStoreApp.getInstance().getResponseHandler()
                    .handleException(response.errorBody().toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MovieStoreApp.getInstance().getResponseHandler().handleException(e)
        }

    }

    private fun handleResult(result: Resource<List<ResultDatabaseModel>>) {

        CoroutineScope(Dispatchers.Default).launch {
            when (result.status) {
                Status.ERROR -> {
                    withContext(Dispatchers.IO) {
                        Log.e("NETWORK ERROR", result.message.toString())
                    }
                }
                Status.SUCCESS -> {
                    result.data?.forEach {
                        addMoviesToDb(it)
                    }
                }
                Status.LOADING -> {
                    Log.e("NETWORK LOADING", result.message.toString())
                }
                else -> {
                    Log.e("NETWORK ERROR", result.message.toString())
                }
            }
        }
    }

    private suspend fun addMoviesToDb(movie: ResultDatabaseModel) = db.getMovieDAO().upsert(movie)
    fun getPopularMovies() = db.getMovieDAO().getAllPopularMovies()
    fun getFavouriteMovies() = db.getFavouriteMoviesDao().getAllFavouriteMovie()
    suspend fun deleteMovie(movie: FavouriteResultDatabaseModel) =
        db.getFavouriteMoviesDao().deleteMovie(movie)

    suspend fun upsertFavouriteMoviesToDb(movie: FavouriteResultDatabaseModel) =
        db.getFavouriteMoviesDao().upsert(movie)

    fun getUpcomingMovies() = db.getMovieDAO().getAllUpcomingMovies()
}

