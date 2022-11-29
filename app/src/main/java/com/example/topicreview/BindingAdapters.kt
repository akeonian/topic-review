package com.example.topicreview

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.topicreview.adapters.AllTopicAdapter
import com.example.topicreview.database.Topic

@BindingAdapter("allTopics")
fun bindAllTopics(allTopicsView: RecyclerView, data: List<Topic>?) {
    allTopicsView.adapter?.let {
        (it as AllTopicAdapter).submitList(data)
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