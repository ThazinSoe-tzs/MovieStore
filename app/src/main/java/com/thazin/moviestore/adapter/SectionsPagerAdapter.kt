package com.thazin.moviestore.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.thazin.moviestore.R
import com.thazin.moviestore.utils.Constants
import com.thazin.moviestore.view.fragment.MoviesFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_upcoming,
    R.string.tab_text_popular,
    R.string.tab_text_favourite
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MoviesFragment(context.getString(R.string.tab_text_upcoming))
            }
            1 -> {
                MoviesFragment(context.getString(R.string.tab_text_popular))
            }
            else -> {
                MoviesFragment(context.getString(R.string.tab_text_favourite))
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return Constants.TAB_COUNT
    }
}