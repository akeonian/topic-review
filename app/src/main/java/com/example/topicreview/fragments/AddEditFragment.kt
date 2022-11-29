package com.example.topicreview.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.topicreview.R
import com.example.topicreview.TopicReviewApplication
import com.example.topicreview.data.UIPreferencesDataStore
import com.example.topicreview.data.homeDataStore
import com.example.topicreview.database.Category
import com.example.topicreview.database.Topic
import com.example.topicreview.databinding.FragmentAddEditBinding
import com.example.topicreview.viewmodels.ReviewViewModel

class AddEditFragment: Fragment() {

    private val navArgs: AddEditFragmentArgs by navArgs()

    private val viewModel: ReviewViewModel by activityViewModels {
        ReviewViewModel.Factory(
            (activity?.application as TopicReviewApplication).database.reviewDao(),
            UIPreferencesDataStore(requireContext().homeDataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddEditBinding.inflate(
            inflater, container, false)
        bindViews(binding)
        return binding.root
    }

    private fun bindViews(binding: FragmentAddEditBinding) {
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        if (navArgs.itemId == 0) {
            when(navArgs.itemType) {
                ItemType.TOPIC -> bindViewsToAddTopic(binding)
                ItemType.CATEGORY -> bindViewsToAddCategory(binding)
            }
        } else {
            when(navArgs.itemType) {
                ItemType.TOPIC -> bindViewsToEditTopic(binding, navArgs.itemId)
                ItemType.CATEGORY -> bindViewsToEditCategory(binding, navArgs.itemId)
            }
        }
    }

    private fun bindViewsToAddTopic(binding: FragmentAddEditBinding) {
        activity?.title = getString(R.string.add)

        binding.saveButton.setOnClickListener {
            saveTopic(binding)
        }
    }

    private fun bindViewsToAddCategory(binding: FragmentAddEditBinding) {
        activity?.title = getString(R.string.add)

        binding.saveButton.setOnClickListener {
            saveCategory(binding)
        }
    }

    private fun bindViewsToEditTopic(binding: FragmentAddEditBinding, topicId: Int) {

        activity?.title = getString(R.string.edit)

        binding.saveButton.isEnabled = false
        viewModel.getTopic(topicId).observe(viewLifecycleOwner) {
            binding.saveButton.isEnabled = true
            bindViewForEdit(binding, it)
        }

    }

    private fun bindViewsToEditCategory(binding: FragmentAddEditBinding, categoryId: Int) {
        activity?.title = getString(R.string.edit)

        binding.saveButton.isEnabled = false
        viewModel.getCategory(categoryId).observe(viewLifecycleOwner) {
            binding.saveButton.isEnabled = true
            bindViewForEdit(binding, it)
        }
    }

    private fun bindViewForEdit(binding: FragmentAddEditBinding, topic: Topic) {
        binding.title.setText(
            topic.title, TextView.BufferType.EDITABLE)

        binding.saveButton.setOnClickListener {
            saveTopic(binding, topic)
        }
    }

    private fun bindViewForEdit(binding: FragmentAddEditBinding, category: Category) {
        binding.title.setText(
            category.title, TextView.BufferType.EDITABLE)

        binding.saveButton.setOnClickListener {
            saveCategory(binding, category)
        }
    }

    private fun saveCategory(binding: FragmentAddEditBinding, category: Category? = null) {
        val title = binding.title.text.toString()
        if (viewModel.isValidCategoryInput(title)) {
            if (category == null) {
                viewModel.insertCategory(title)
            } else {
                viewModel.editCategory(category, title)
            }
            findNavController().navigateUp()
        } else {
            showInvalidInput()
        }
    }

    private fun saveTopic(binding: FragmentAddEditBinding, topic: Topic? = null) {
        val title = binding.title.text.toString()
        if (viewModel.isValidTopicInput(title)) {
            Log.d("TAG", "saveTopic=$topic")
            if (topic == null) {
                viewModel.insertTopic(title)
            } else {
                viewModel.editTopic(topic, title)
            }
            findNavController().navigateUp()
        } else {
            showInvalidInput()
        }
    }

    private fun showInvalidInput() {
        Toast.makeText(
            requireContext(),
            R.string.invalid_input_message,
            Toast.LENGTH_SHORT
        ).show()
    }

    /*
    TODO: Remove this comment after testing add and edit
    companion object {

        fun createTopicInstance(id: Int = 0): AddEditFragment {
            val args = Bundle()
            args.putInt(KEY_ID, id)
            args.putInt(KEY_TYPE, TYPE_TOPIC)
            val dialog = AddEditFragment()
            dialog.arguments = args
            return dialog
        }

        fun createCategoryInstance(id: Int = 0): AddEditFragment {
            val args = Bundle()
            args.putInt(KEY_ID, id)
            args.putInt(KEY_TYPE, TYPE_CATEGORY)
            val dialog = AddEditFragment()
            dialog.arguments = args
            return dialog
        }

    }
    */
}

enum class ItemType {
    TOPIC, CATEGORY
}