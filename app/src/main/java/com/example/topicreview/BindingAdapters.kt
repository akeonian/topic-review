package com.example.topicreview

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.topicreview.adapters.AllReviewItemsAdapter
import com.example.topicreview.models.ReviewItem

@BindingAdapter("allReviewItems")
fun bindAllReviewItems(allItemsView: RecyclerView, data: List<ReviewItem>?) {
    allItemsView.adapter?.let {
        (it as AllReviewItemsAdapter).submitList(data)
    }
}

@BindingAdapter("listPlaceholder")
fun bindPlaceholder(placeholder: View, data: List<Any?>?) {
    placeholder.visibility = if (data.isNullOrEmpty()) {
        View.VISIBLE
    } else {
        View.GONE
    }
}