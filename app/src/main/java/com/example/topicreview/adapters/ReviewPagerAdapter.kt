package com.example.topicreview.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.topicreview.R
import com.example.topicreview.fragments.AllFragment
import com.example.topicreview.fragments.DueListFragment

class ReviewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val titles = arrayOf(
        fragmentActivity.getString(R.string.due_title),
        fragmentActivity.getString(R.string.all_title)
    )

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> AllFragment()
            else -> DueListFragment()
        }
    }

    fun getTitle(position: Int): String {
        return titles[position]
    }
}