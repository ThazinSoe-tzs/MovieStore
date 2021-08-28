package com.thazin.moviestore.db

import androidx.room.*
import com.thazin.moviestore.models.ResultDatabaseModel
import com.thazin.moviestore.models.FavouriteResultDatabaseModel

@Dao
interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(movie : ResultDatabaseModel): Long

    @Query("SELECT * FROM movies WHERE popular")
    fun getAllPopularMovies():List<ResultDatabaseModel>
    @Query("SELECT * FROM movies WHERE latest")
    fun getAllUpcomingMovies():List<ResultDatabaseModel>

    @Delete
    suspend fun deleteMovie(movie: ResultDatabaseModel)
}

@Dao
interface FavouriteMoviesDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(movie : FavouriteResultDatabaseModel): Long

    @Query("SELECT * FROM favourite_movies")
    fun getAllFavouriteMovie(): List<FavouriteResultDatabaseModel>

    @Delete
    suspend fun deleteMovie(movie: FavouriteResultDatabaseModel)
}