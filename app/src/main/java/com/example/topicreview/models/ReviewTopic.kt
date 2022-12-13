package com.example.topicreview.models

import com.example.topicreview.database.Topic
import com.example.topicreview.utils.getDaysFrom
import java.util.*

class ReviewTopic(
    id: Int,
    title: String,
    val dueDate: Calendar,
    val dateCreated: Calendar,
    val lastReviewDate: Calendar,
    val lastDuration: Int
    ): ReviewItem(id, title, dueDate.getDaysFrom()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ReviewTopic

        if (dueDate != other.dueDate) return false
        if (dateCreated != other.dateCreated) return false
        if (lastReviewDate != other.lastReviewDate) return false
        if (lastDuration != other.lastDuration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + dueDate.hashCode()
        result = 31 * result + dateCreated.hashCode()
        result = 31 * result + lastReviewDate.hashCode()
        result = 31 * result + lastDuration
        return result
    }
}

fun ReviewTopic.toTopic(
    id: Int = this.id,
    title: String = this.title,
    dateCreated: Calendar = this.dateCreated,
    dueDate: Calendar = this.dueDate,
    lastReviewDate: Calendar = this.lastReviewDate,
    lastDuration: Int = this.lastDuration) =
    Topic(
        id = id,
        title = title,
        dateCreated = dateCreated,
        dueDate = dueDate,
        lastReviewDate = lastReviewDate,
        lastDuration = lastDuration
    )