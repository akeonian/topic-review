package com.example.topicreview.fragments

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.topicreview.EXPORT_FILE_NAME
import com.example.topicreview.MIME_TYPE_CSV
import com.example.topicreview.R
import com.example.topicreview.TopicReviewApplication
import com.example.topicreview.adapters.AllReviewItemsAdapter
import com.example.topicreview.data.UIPreferencesDataStore
import com.example.topicreview.data.homeDataStore
import com.example.topicreview.databinding.FragmentAllBinding
import com.example.topicreview.models.ReviewCategory
import com.example.topicreview.models.ReviewTopic
import com.example.topicreview.repository.ReviewRepository
import com.example.topicreview.viewmodels.ReviewViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AllFragment: Fragment(), MenuProvider {

    private val viewModel: ReviewViewModel by activityViewModels {
        val reviewDao = (activity?.application as TopicReviewApplication).database.reviewDao()
        ReviewViewModel.Factory(
            reviewDao,
            ReviewRepository(reviewDao),
            UIPreferencesDataStore(requireContext().homeDataStore)
        )
    }

    private val importContent = registerForActivityResult(GetContent()) { contentUri ->
        contentUri?.let { viewModel.importData(it, requireContext().contentResolver) }
    }
    private val exportContent = registerForActivityResult(CreateDocument(MIME_TYPE_CSV)) { uri ->
        uri?.let {
            viewModel.exportData(it, requireContext().contentResolver)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAllBinding.inflate(
            inflater, container, false)
        binding.allFragment = this@AllFragment
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = AllReviewItemsAdapter (
            { showTopicOptions(it) },
            { showCategoryOptions(it) }
        )
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this)
    }

    fun openAddScreen() {
        MaterialAlertDialogBuilder(requireContext())
            .setItems(R.array.item_types) { _, index ->
                if (index == 1) {
                    openAddCategoryInstance()
                } else {
                    openAddTopicInstance()
                }
            }
            .show()
    }

    private fun openAddTopicInstance() {
        val action = MainFragmentDirections.openAddEditFragment(ItemType.TOPIC)
        findNavController().navigate(action)
    }

    private fun openAddCategoryInstance() {
        val action = MainFragmentDirections.openAddEditFragment(ItemType.CATEGORY)
        findNavController().navigate(action)
    }

    private fun showTopicOptions(topic: ReviewTopic) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.options)
            .setItems(R.array.all_options) {_,index->
                handleOptions(topic, index)
            }
            .show()
    }

    private fun handleOptions(topic: ReviewTopic, index: Int) {
        if (index == 1) {
            viewModel.delete(topic)
        } else {
            showEditDialog(topic)
        }
    }

    private fun showEditDialog(topic: ReviewTopic) {
        val action = MainFragmentDirections.openAddEditFragment(
            itemId = topic.id, itemType = ItemType.TOPIC)
        findNavController().navigate(action)
    }

    private fun showCategoryOptions(category: ReviewCategory) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.options)
            .setItems(R.array.all_category_options) {_,index->
                handleOptions(category, index)
            }
            .show()
    }

    private fun handleOptions(category: ReviewCategory, index: Int) {
        if (index == 1) {
            viewModel.delete(category)
        } else {
            showEditDialog(category)
        }
    }

    private fun showEditDialog(category: ReviewCategory) {
        val action = MainFragmentDirections.openAddEditFragment(
            itemId = category.id, itemType = ItemType.CATEGORY)
        findNavController().navigate(action)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.all_topics, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.importData -> {
                try {
                    importContent.launch(MIME_TYPE_CSV)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        requireContext(),
                        R.string.file_picker_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true
            }
            R.id.exportData -> {
                try {
                    exportContent.launch(EXPORT_FILE_NAME)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        requireContext(),
                        R.string.file_picker_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                false
            }
            else -> false
        }
    }

}