package com.thazin.moviestore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.thazin.moviestore.R
import com.thazin.moviestore.databinding.ItemMovieBinding
import com.thazin.moviestore.extension.toast
import com.thazin.moviestore.models.Result
import com.thazin.moviestore.utils.Constants
import kotlinx.android.synthetic.main.item_movie.view.*

class MoviesAdapter(context: Context, private val callback: (Result) -> Unit) : RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>()  {

    private var type: String = ""
    var context = context
    inner class MoviesViewHolder(private val itemViewBinding: ItemMovieBinding) :
        RecyclerView.ViewHolder(
            itemViewBinding.root
        ) {

        fun bindView(movieItem: Result) {
            itemViewBinding.apply {
                if (type == context.getString(R.string.tab_text_favourite)){
                    btnFavourite.visibility = View.GONE
                }
                else {
                    btnFavourite.visibility = View.VISIBLE
                }
                tvMovieName.text = movieItem.title
                Glide.with(context).load(Constants.IMAGE_BASE_URL+movieItem.poster_path).diskCacheStrategy(
                    DiskCacheStrategy.ALL
                ).into(ivPoster)
            }

            itemViewBinding.root.btn_favourite.setOnClickListener{
                context?.toast(context.getString(R.string.favourite_success))
                callback(movieItem)
            }


            itemViewBinding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(movieItem)
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val binding = ItemMovieBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MoviesViewHolder(binding)
    }

    private var onItemClickListener: ((Result) -> Unit)? = null
    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val movieItem = differ.currentList[position]
        holder.bindView(movieItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setOnItemClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }

    fun setStatus(type: String){
        this.type = type
    }
}