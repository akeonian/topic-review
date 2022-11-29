package com.example.topicreview.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.topicreview.database.Topic

object TopicDiffCallback: DiffUtil.ItemCallback<Topic>() {

    override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
        return oldItem == newItem
    }
}