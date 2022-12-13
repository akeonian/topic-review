package com.example.topicreview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.topicreview.R
import com.example.topicreview.databinding.ListItemDueCategoryBinding
import com.example.topicreview.databinding.ListItemDueTopicBinding
import com.example.topicreview.models.ReviewCategory
import com.example.topicreview.models.ReviewItem
import com.example.topicreview.models.ReviewTopic

class DueItemsAdapter(
    private val onTopicClickListener: (ReviewTopic) -> Unit
) : ListAdapter<ReviewItem, ViewHolder>(ReviewItemDiffCallback) {

    private val VIEW_TYPE_TOPIC = 0
    private val VIEW_TYPE_CATEGORY = 1

    class TopicViewHolder private constructor(
        view: View,
        private val binding: ListItemDueTopicBinding
    ) : ViewHolder(view) {
        fun bind(topic: ReviewTopic) {
            binding.title.text = topic.title
        }

        companion object {
            fun createInstance(parent: ViewGroup): TopicViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val main = inflater.inflate(R.layout.list_item_main, parent, false) as ViewGroup
                val binding = ListItemDueTopicBinding.inflate(
                    inflater, main, false
                )
                main.addView(binding.root)
                return TopicViewHolder(main, binding)
            }
        }
    }

    private class CategoryViewHolder private constructor(
        view: View, val binding: ListItemDueCategoryBinding
    ) : ViewHolder(view) {

        fun bind(item: ReviewCategory, onTopicClickListener: (ReviewTopic) -> Unit) {
            binding.title.text = item.title

            itemView.setOnClickListener {
                binding.recyclerView.visibility = if (binding.recyclerView.visibility == GONE) {
                    if (item.subItems.isNotEmpty()) {
                        val adapter = DueItemsAdapter(onTopicClickListener)
                        adapter.submitList(item.subItems)
                        binding.recyclerView.adapter = adapter
                    }
                    VISIBLE
                } else GONE
            }
        }

        companion object {

            fun createInstance(parent: ViewGroup): CategoryViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val main = inflater.inflate(R.layout.list_item_main, parent, false) as ViewGroup
                val binding = ListItemDueCategoryBinding.inflate(
                    inflater, main, false
                )
                main.addView(binding.root)
                return CategoryViewHolder(main, binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TOPIC -> TopicViewHolder.createInstance(parent)
            VIEW_TYPE_CATEGORY -> CategoryViewHolder.createInstance(parent)
            else -> throw IllegalStateException("Unknown view type=$viewType")
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
            (holder as CategoryViewHolder).bind(item, onTopicClickListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is ReviewTopic -> VIEW_TYPE_TOPIC
            is ReviewCategory -> VIEW_TYPE_CATEGORY
            else -> throw IllegalStateException("Unknown class type=${item.javaClass}")
        }
    }

}