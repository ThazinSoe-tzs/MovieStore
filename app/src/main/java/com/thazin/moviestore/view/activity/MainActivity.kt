package com.thazin.moviestore.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.thazin.moviestore.R
import com.thazin.moviestore.adapter.SectionsPagerAdapter
import com.thazin.moviestore.databinding.ActivityMainBinding
import com.thazin.moviestore.db.MovieDatabase
import com.thazin.moviestore.repository.MovieRepository
import com.thazin.moviestore.utils.DataStoreManager
import com.thazin.moviestore.utils.UiMode
import com.thazin.moviestore.view.fragment.MoviesFragment
import com.thazin.moviestore.viewmodel.MovieViewModel
import com.thazin.moviestore.viewmodel.MovieViewModelProvideFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MovieViewModel
    lateinit var repository : MovieRepository
    private lateinit var dataStoreManager: DataStoreManager
    var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStoreManager = DataStoreManager(applicationContext)
        observeUiPreferences()
        repository = MovieRepository(MovieDatabase.invoke(this))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        val viewModelProviderFactory = MovieViewModelProvideFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MovieViewModel::class.java)

        fab.setOnClickListener {
            MoviesFragment(getString(R.string.tab_text_favourite))
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