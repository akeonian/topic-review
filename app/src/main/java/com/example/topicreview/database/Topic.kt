package com.example.topicreview.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.topicreview.models.ReviewTopic
import java.util.*

@Entity
data class Topic(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    @ColumnInfo(name = "date_created") val dateCreated: Calendar,
    @ColumnInfo(name = "date_due") val dueDate: Calendar,
    @ColumnInfo(name = "date_last_review") val lastReviewDate: Calendar,
    // last duration is, No of days
    @ColumnInfo(name = "last_duration") val lastDuration: Int = 0,
    @ColumnInfo(name = "category_id") val categoryId: Int = -1
)

fun Topic.asReviewTopic(): ReviewTopic {
    return ReviewTopic(
        id = id,
        title = title,
        dateCreated = dateCreated,
        dueDate = dueDate,
        lastReviewDate = lastReviewDate,
        lastDuration = lastDuration
    )
}