package com.thazin.moviestore.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName ="movies"
    )
@Parcelize
data class ResultDatabaseModel(

    @PrimaryKey
    val id: String,
    val backdrop_path: String,
    val original_title: String,
    val overview: String,
    val popularity: String,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val vote_average: String,
    val popular : Boolean,
    val latest : Boolean
) : Parcelable

fun List<ResultDatabaseModel>.asDomainModel(): List<Result> {
    return map {
        Result(
            id = it.id,
            backdrop_path = it.backdrop_path,
            original_title = it.original_title,
            overview = it.overview,
            popularity = it.popularity,
            poster_path = it.poster_path,
            release_date = it.release_date,
            title = it.title,
            vote_average = it.vote_average,
            popular = it.popular,
            latest = it.latest

        )
    }
}

@Entity(
    tableName ="favourite_movies"
)
@Parcelize
data class FavouriteResultDatabaseModel(

    @PrimaryKey
    val id: String,
    val backdrop_path: String,
    val original_title: String,
    val overview: String,
    val popularity: String,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val vote_average: String,
    val popular : Boolean,
    val latest : Boolean,
) : Parcelable

fun List<FavouriteResultDatabaseModel>.toDomainModel(): List<Result> {
    return map {
        Result(
            id = it.id,
            backdrop_path = it.backdrop_path,
            original_title = it.original_title,
            overview = it.overview,
            popularity = it.popularity,
            poster_path = it.poster_path,
            release_date = it.release_date,
            title = it.title,
            vote_average = it.vote_average,
            latest = it.latest,
            popular = it.popular
        )
    }
}