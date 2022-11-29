package com.example.topicreview.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.topicreview.adapters.ReviewPagerAdapter
import com.example.topicreview.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)

        val adapter = ReviewPagerAdapter(requireActivity())
        binding.reviewPager.adapter = adapter
        TabLayoutMediator(
            binding.tabLayout,
            binding.reviewPager
        ) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        return binding.root
    }
}