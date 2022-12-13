package com.example.topicreview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.topicreview.R
import com.example.topicreview.databinding.ListItemAllCategoryBinding
import com.example.topicreview.databinding.ListItemAllTopicBinding
import com.example.topicreview.models.ReviewCategory
import com.example.topicreview.models.ReviewItem
import com.example.topicreview.models.ReviewTopic

class AllReviewItemsAdapter(
    private val onTopicClickListener: (ReviewTopic) -> Unit,
    private val onCategoryLongClickListener: (ReviewCategory) -> Unit
): ListAdapter<ReviewItem, ViewHolder>(ReviewItemDiffCallback) {

    private val VIEW_TYPE_TOPIC = 0
    private val VIEW_TYPE_CATEGORY = 1

    private class TopicViewHolder private constructor(
        view: View, val binding: ListItemAllTopicBinding) : ViewHolder(view) {

        fun bind(item: ReviewTopic) {
            binding.title.text = item.title
            val days = item.daysLeft
            if (days <= 0) {
                binding.days.setText(R.string.due)
            } else {
                binding.days.text = itemView.resources
                    .getQuantityString(R.plurals.due_days, days, days)
            }
        }

        companion object {

            fun createInstance(parent: ViewGroup): TopicViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val main = inflater.inflate(R.layout.list_item_main, parent, false) as ViewGroup
                val binding = ListItemAllTopicBinding.inflate(
                    inflater, main, false)
                main.addView(binding.root)
                return TopicViewHolder(main, binding)
            }
        }
    }

    private class CategoryViewHolder private constructor(
        view: View, val binding: ListItemAllCategoryBinding) : ViewHolder(view) {

        fun bind(
            item: ReviewCategory,
            onTopicClickListener: (ReviewTopic) -> Unit,
            onCategoryLongClickListener: (ReviewCategory) -> Unit) {
            binding.title.text = item.title
            val days = item.daysLeft
            if (days <= 0) {
                binding.days.setText(R.string.due)
            } else {
                binding.days.text = itemView.resources
                    .getQuantityString(R.plurals.due_days, days, days)
            }

            itemView.setOnClickListener {
                binding.recyclerView.visibility = if (binding.recyclerView.visibility == GONE) {
                    if (item.subItems.isNotEmpty()) {
                        val adapter = AllReviewItemsAdapter(
                            onTopicClickListener, onCategoryLongClickListener)
                        adapter.submitList(item.subItems)
                        binding.recyclerView.adapter = adapter
                    }
                    VISIBLE
                } else GONE
            }
            itemView.setOnLongClickListener {
                onCategoryLongClickListener(item)
                true
            }
        }

        companion object {

            fun createInstance(parent: ViewGroup): CategoryViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val main = inflater.inflate(R.layout.list_item_main, parent, false) as ViewGroup
                val binding = ListItemAllCategoryBinding.inflate(
                    inflater, main, false)
                main.addView(binding.root)
                return CategoryViewHolder(main, binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TOPIC -> TopicViewHolder.createInstance(parent)
            VIEW_TYPE_CATEGORY -> CategoryViewHolder.createInstance(parent)
            else -> throw IllegalStateException("unknown view type=$viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item is ReviewTopic) {
            (holder as TopicViewHolder).bind(item)
            holder.itemView.setOnClickListener {
                onTopicClickListener(item)
            }
        } else if (item is ReviewCategory) {
            (holder as CategoryViewHolder).bind(
                item, onTopicClickListener, onCategoryLongClickListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is ReviewTopic -> VIEW_TYPE_TOPIC
            is ReviewCategory -> VIEW_TYPE_CATEGORY
            else -> throw IllegalStateException("Unknown class Type=${item.javaClass}" )
        }
    }

}