package com.example.topicreview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.topicreview.R
import com.example.topicreview.database.Topic
import com.example.topicreview.databinding.ListItemDueTopicBinding

class DueTopicsAdapter(
    private val onItemClick: (Topic) -> Unit
): ListAdapter<Topic, DueTopicsAdapter.ViewHolder>(TopicDiffCallback) {

    class ViewHolder private constructor(view: View, private val binding: ListItemDueTopicBinding): RecyclerView.ViewHolder(view) {
        fun bind(topic: Topic) {
            binding.title.text = topic.title
        }

        companion object {
            fun createInstance(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val main = inflater.inflate(R.layout.list_item_main, parent, false) as ViewGroup
                val binding = ListItemDueTopicBinding.inflate(
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