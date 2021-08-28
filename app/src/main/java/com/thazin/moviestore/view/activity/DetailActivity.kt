package com.thazin.moviestore.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.thazin.moviestore.R
import com.thazin.moviestore.databinding.ActivityDetailBinding
import com.thazin.moviestore.db.MovieDatabase
import com.thazin.moviestore.models.Result
import com.thazin.moviestore.models.asDataBaseModel
import com.thazin.moviestore.repository.MovieRepository
import com.thazin.moviestore.utils.Constants
import com.thazin.moviestore.utils.DataStoreManager
import com.thazin.moviestore.utils.UiMode
import com.thazin.moviestore.viewmodel.MovieViewModel
import com.thazin.moviestore.viewmodel.MovieViewModelProvideFactory
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityDetailBinding
    lateinit var viewModel: MovieViewModel
    lateinit var repository : MovieRepository
    private lateinit var dataStoreManager: DataStoreManager
    var isDarkMode = false
    private lateinit var movie: Result

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        dataStoreManager = DataStoreManager(applicationContext)
        observeUiPreferences()
        repository = MovieRepository(MovieDatabase.invoke(this))
        val viewModelProviderFactory = MovieViewModelProvideFactory(application, repository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MovieViewModel::class.java)

        val bundle = intent.extras
        if (bundle != null) {
            movie = intent.getParcelableExtra<Result>("movie")!!
            Glide.with(this).load(Constants.IMAGE_BASE_URL + movie.backdrop_path)
                .diskCacheStrategy(
                    DiskCacheStrategy.ALL
                ).into(iv_backdrop)
            tv_rating.text = getString(R.string.rating, movie.vote_average)
            tv_overview.text = movie.overview
            tv_release_date.text = movie.release_date
            tv_name.text = movie.title
            fab_add_favourite.setOnClickListener { view ->
                    viewModel.saveMovie(movie.asDataBaseModel())
                Snackbar.make(view, getString(R.string.favourite_success), Snackbar.LENGTH_LONG)
                    .setAction("OK", null).show()
            }
        }

    }

    private fun observeUiPreferences() {
        dataStoreManager.uiModeFlow.asLiveData().observe(this) { uiMode ->
            when (uiMode) {
                UiMode.LIGHT -> removeDarkMode()
                UiMode.DARK -> applyDarkMode()
                else -> removeDarkMode()
            }
        }
    }
    private fun removeDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()
        isDarkMode = false
    }

    private fun applyDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
        isDarkMode = true
    }
}