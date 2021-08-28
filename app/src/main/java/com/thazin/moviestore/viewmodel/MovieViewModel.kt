package com.thazin.moviestore.viewmodel

import android.app.Application

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import androidx.lifecycle.*
import com.thazin.moviestore.repository.MovieRepository
import com.thazin.moviestore.application.MovieStoreApp
import com.thazin.moviestore.models.Result
import com.thazin.moviestore.models.ResultDatabaseModel
import com.thazin.moviestore.models.FavouriteResultDatabaseModel
import com.thazin.moviestore.models.asDomainModel
import com.thazin.moviestore.models.toDomainModel
import com.thazin.moviestore.utils.*

class MovieViewModel(app: Application, private val movieRepository: MovieRepository) :
    AndroidViewModel(app) {

    private val latestMoviesMutable: MutableLiveData<Resource<List<Result>>> =
        MutableLiveData()
    val upcomingMovies: LiveData<Resource<List<Result>>>
        get() = latestMoviesMutable
    private val favouriteMoviesMutable: MutableLiveData<Resource<List<Result>>> =
        MutableLiveData()
    val favouriteMovies: LiveData<Resource<List<Result>>>
        get() = favouriteMoviesMutable
    private val popularMoviesMutable: MutableLiveData<Resource<List<Result>>> =
        MutableLiveData()
    val popularMovies: LiveData<Resource<List<Result>>>
        get() = popularMoviesMutable

    @FlowPreview
    var state = NetworkStatusTracker(app).networkStatus
        .map(
            onAvailable = { MyState.Fetched },
            onUnavailable = { MyState.Error }
        )
        .asLiveData(Dispatchers.IO)

    init {
        val isFirstTime =
            DataStoreManager(MovieStoreApp.getInstance().baseContext).isFirstTimeFlow.asLiveData(
                Dispatchers.IO).value
        if (isFirstTime != null) {
            when (isFirstTime) {
                IsFirst.FIRST -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        movieRepository.refreshLatestMovieList()
                        movieRepository.refreshPopularMovieList()
                        DataStoreManager(MovieStoreApp.getInstance().baseContext).setFirstTime(
                            IsFirst.NO)
                    }
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                movieRepository.refreshLatestMovieList()
                movieRepository.refreshPopularMovieList()
                DataStoreManager(MovieStoreApp.getInstance().baseContext).setFirstTime(
                    IsFirst.NO)
            }
        }

        getPopularMovies()
        getUpcomingMovies()
    }

    private fun getUpcomingMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            handleMoviesResponse(movieRepository.getPopularMovies(), popularMoviesMutable)
        }
    }

    private fun getPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            handleMoviesResponse(movieRepository.getUpcomingMovies(), latestMoviesMutable)
        }
    }

    private fun handleMoviesResponse(movies: List<ResultDatabaseModel>, list: MutableLiveData<Resource<List<Result>>>, ) {
        list.postValue(Resource.Loading())
        if (movies.asDomainModel().isNotEmpty()) {
            list.postValue(Resource.Success(movies.asDomainModel(),
                responseCode = 0))
        } else {
            list.postValue(Resource.Error("No Movies Found",
                responseCode = 0, data = emptyList()))
        }
    }


    fun saveMovie(movie: FavouriteResultDatabaseModel) = viewModelScope.launch {
        movieRepository.upsertFavouriteMoviesToDb(movie)
    }

    fun getSavedMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val savedMovies = movieRepository.getFavouriteMovies()

            favouriteMoviesMutable.postValue(Resource.Success(savedMovies.toDomainModel() as MutableList<com.thazin.moviestore.models.Result>,
                0))
        }
    }

    fun deleteMovie(movie: FavouriteResultDatabaseModel) = viewModelScope.launch(Dispatchers.IO) {
        movieRepository.deleteMovie(movie)
        movieRepository.getFavouriteMovies()
    }

}