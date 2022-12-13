package com.example.topicreview.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.topicreview.models.ReviewItem

object ReviewItemDiffCallback: DiffUtil.ItemCallback<ReviewItem>() {

    override fun areItemsTheSame(oldItem: ReviewItem, newItem: ReviewItem): Boolean {
        return oldItem.id == newItem.id && oldItem::class == newItem::class
    }

    override fun areContentsTheSame(oldItem: ReviewItem, newItem: ReviewItem): Boolean {
        return oldItem == newItem
    }
}