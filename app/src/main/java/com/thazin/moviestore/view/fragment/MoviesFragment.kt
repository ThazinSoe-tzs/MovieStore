package com.thazin.moviestore.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.thazin.moviestore.R
import com.thazin.moviestore.adapter.MoviesAdapter
import com.thazin.moviestore.databinding.FragmentMainBinding
import com.thazin.moviestore.extension.hide
import com.thazin.moviestore.extension.show
import com.thazin.moviestore.extension.toast
import com.thazin.moviestore.models.asDataBaseModel
import com.thazin.moviestore.utils.Resource
import com.thazin.moviestore.view.activity.DetailActivity
import com.thazin.moviestore.viewmodel.MovieViewModel
import kotlinx.android.synthetic.main.fragment_main.*

class MoviesFragment(type: String) : Fragment(R.layout.fragment_main) {

    var type: String = type
    private val viewModel: MovieViewModel by activityViewModels()
    lateinit var moviesAdapter: MoviesAdapter
    private lateinit var binding: FragmentMainBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        setUpRecyclerView()

        moviesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                val bundle = Bundle()
                bundle.putParcelable("movie", it)
                var intent = Intent(context, DetailActivity::class.java)
                intent.putExtras(bundle)
                context?.startActivity(intent)
            }
        }

        when (type) {
            getString(R.string.tab_text_popular) -> fetchPopular()
            getString(R.string.tab_text_upcoming) -> fetchUpcoming()
            else -> {
                moviesAdapter.setStatus(getString(R.string.tab_text_favourite))
                moviesAdapter.notifyDataSetChanged()
                observeMutables()
                viewModel.getSavedMovies()
                setupSwipeToDeleteFunction()
            }
        }
    }

    private fun fetchUpcoming() {
        viewModel.upcomingMovies.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    progressBar.hide()
                    //database to domain model
                    moviesAdapter.differ.submitList(response.data!!)
                }
                is Resource.Loading -> {
                    progressBar.show()
                }
                is Resource.Error -> {
                    response.message.let { message ->
                        progressBar.hide()
                        context?.toast(message.toString())
                    }
                }
                else -> {
                    context?.toast(response.message.toString())
                }
            }
        })
    }

    private fun fetchPopular() {
        viewModel.popularMovies.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    progressBar.hide()
                    //database to domain model
                    moviesAdapter.differ.submitList(response.data!!)
                }
                is Resource.Loading -> {
                    progressBar.show()
                }
                is Resource.Error -> {
                    response.message.let { message ->
                        progressBar.hide()
                        context?.toast(message.toString())
                    }
                }
                else -> {
                    context?.toast(response.message.toString())
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        moviesAdapter = MoviesAdapter(requireActivity().applicationContext) {
            viewModel.saveMovie(it.asDataBaseModel())
            moviesAdapter.notifyDataSetChanged()
        }
        binding.movieListRv.adapter = moviesAdapter
    }

    private fun observeMutables() {
        viewModel.favouriteMovies.observe(viewLifecycleOwner, { movies ->
            Log.e("data", "changed")
            moviesAdapter.differ.submitList(movies.data)
        })
    }

    private fun setupSwipeToDeleteFunction() {
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val movie = moviesAdapter.differ.currentList[position]
                viewModel.deleteMovie(movie = movie.asDataBaseModel())
                Snackbar.make(requireView(), "Successfully deleted ", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveMovie(movie.asDataBaseModel())
                    }
                }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.movieListRv)
        }
    }
}
