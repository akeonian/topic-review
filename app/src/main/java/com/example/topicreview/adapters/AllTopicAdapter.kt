package com.example.topicreview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.topicreview.R
import com.example.topicreview.database.Topic
import com.example.topicreview.databinding.ListItemAllTopicBinding
import com.example.topicreview.utils.getDaysFrom

class AllTopicAdapter(
    private val onItemClick: (Topic) -> Unit
): ListAdapter<Topic, AllTopicAdapter.ViewHolder>(TopicDiffCallback) {

    class ViewHolder private constructor(view: View, private val binding: ListItemAllTopicBinding): RecyclerView.ViewHolder(view) {
        fun bind(topic: Topic) {
            binding.title.text = topic.title
            val days = topic.dueDate.getDaysFrom()
            if (days <= 0) {
                binding.days.setText(R.string.due)
            } else {
                binding.days.text = itemView.resources
                    .getQuantityString(R.plurals.due_days, days, days)
            }
        }

        companion object {
            fun createInstance(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val main = inflater.inflate(R.layout.list_item_main, parent, false) as ViewGroup
                val binding = ListItemAllTopicBinding.inflate(
                    inflater, main, false)
                main.addView(binding.root)
                return ViewHolder(main, binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.createInstance(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = getItem(position)
        holder.bind(topic)
        holder.itemView.setOnClickListener {
            onItemClick(topic)
        }
    }

}