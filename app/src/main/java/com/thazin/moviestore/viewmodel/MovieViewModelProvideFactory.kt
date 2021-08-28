package com.thazin.moviestore.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thazin.moviestore.repository.MovieRepository

class MovieViewModelProvideFactory (val app: Application, private val movieRepository: MovieRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return  MovieViewModel(app ,movieRepository ) as T
    }
}