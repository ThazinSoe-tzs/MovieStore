package com.thazin.moviestore.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thazin.moviestore.models.ResultDatabaseModel
import com.thazin.moviestore.models.FavouriteResultDatabaseModel

@Database(entities = [ResultDatabaseModel::class, FavouriteResultDatabaseModel::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun getMovieDAO(): MoviesDao
    abstract fun getFavouriteMoviesDao(): FavouriteMoviesDao

    companion object {
        @Volatile
        private var instance: MovieDatabase? = null
        private val Lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(Lock) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(

                context.applicationContext,
                MovieDatabase::class.java,
                "movie_db.db"
            ).build()
    }
}