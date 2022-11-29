package com.example.topicreview.fragments

import UiPreferences
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.topicreview.R
import com.example.topicreview.TopicReviewApplication
import com.example.topicreview.adapters.DueTopicsAdapter
import com.example.topicreview.data.UIPreferencesDataStore
import com.example.topicreview.data.homeDataStore
import com.example.topicreview.database.Topic
import com.example.topicreview.databinding.DialogCustomReviewBinding
import com.example.topicreview.databinding.FragmentDueListBinding
import com.example.topicreview.viewmodels.ReviewViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DueListFragment: Fragment(), MenuProvider {

    private val viewModel: ReviewViewModel by activityViewModels {
        ReviewViewModel.Factory(
            (activity?.application as TopicReviewApplication).database.reviewDao(),
            UIPreferencesDataStore(requireContext().homeDataStore)
        )
    }

    private lateinit var _binding: FragmentDueListBinding
    private val binding get() = _binding

    private lateinit var _adapter: DueTopicsAdapter
    private val adapter get() = _adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDueListBinding.inflate(
            inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO("Implement Categories and sort")

        _adapter = DueTopicsAdapter {
            showReviewDialog(it)
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.adapter = adapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshDueTopics()
        }
        viewModel.allTopic.observe(viewLifecycleOwner) {
            refreshDueTopics()
        }
        viewModel.sorting.observe(viewLifecycleOwner) {
            activity?.invalidateOptionsMenu()
            refreshDueTopics()
        }
        refreshDueTopics()

        // Setup Menu for refresh
        activity?.addMenuProvider(this, viewLifecycleOwner)
    }

    private fun refreshDueTopics() {
        viewModel.latestDueTopics.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.placeholder.visibility =
                if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showReviewDialog(topic: Topic) {
        val days = topic.lastDuration
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.review_dialog_title, days))
            .setItems(R.array.review_options) {_,index->
                updateReviewDate(topic, index)
            }
            .show()
    }

    private fun updateReviewDate(topic: Topic, index: Int) {
        if (index == 3) {
            showCustomSelectDialog(topic)
        } else {
            val days = when (index) {
                1 -> 7
                2 -> 30
                else -> 1
            }
            viewModel.updateTopicReviewDate(topic, days)
        }
    }

    private fun showCustomSelectDialog(topic: Topic) {
        val binding = DialogCustomReviewBinding.inflate(
            LayoutInflater.from(requireContext()))
        binding.topic = topic
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.custom_dialog_title)
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.ok) {_,_->
                val days = binding.noOfDays.text.toString().toIntOrNull()
                days?.let {
                    viewModel.updateTopicReviewDate(topic, it)
                }
            }
            .show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.due_topics, menu)
        when(viewModel.sorting.value) {
            UiPreferences.Sorting.Z_TO_A -> menu.findItem(R.id.z_to_a)?.isChecked = true
            else -> menu.findItem(R.id.a_to_z)?.isChecked = true
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.refreshDueTopics -> {
                binding.swipeRefreshLayout.isRefreshing = true
                refreshDueTopics()
                true
            }
            R.id.a_to_z -> {
                viewModel.setSorting(UiPreferences.Sorting.A_TO_Z)
                true
            }
            R.id.z_to_a -> {
                viewModel.setSorting(UiPreferences.Sorting.Z_TO_A)
                true
            }
            else -> false
        }
    }
}