package com.thazin.moviestore.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName ="movies"
    )
@Parcelize
@JsonClass(generateAdapter = true)
data class Result(

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

fun Result.asDataBaseModel()=
    FavouriteResultDatabaseModel(
        id = this.id!!,
        backdrop_path = this.backdrop_path!!,
        original_title = this.original_title!!,
        overview = this.overview!!,
        popularity = this.popularity!!,
        poster_path = this.poster_path!!,
        release_date = this.release_date!!,
        title = this.title!!,
        vote_average = this.vote_average!!,
        popular = this.popular,
        latest =this.latest

    )
