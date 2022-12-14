package com.example.topicreview

import com.example.topicreview.database.Topic
import java.util.*

object DatabaseTestUtil {

    val testTopics = listOf(
        Topic(
            id = 1,
            title = "title1",
            dateCreated = Calendar.getInstance().apply { timeInMillis = 12341 },
            dueDate = Calendar.getInstance().apply { timeInMillis = 12342 },
            lastReviewDate = Calendar.getInstance().apply { timeInMillis = 12343 },
            lastDuration = 14,
            categoryId = -1
        ),
        Topic(
            id = 2,
            title = "title2",
            dateCreated = Calendar.getInstance().apply { timeInMillis = 22341 },
            dueDate = Calendar.getInstance().apply { timeInMillis = 22342 },
            lastReviewDate = Calendar.getInstance().apply { timeInMillis = 22343 },
            lastDuration = 24,
            categoryId = -1
        ),
        Topic(
            id = 3,
            title = "title3",
            dateCreated = Calendar.getInstance().apply { timeInMillis = 32341 },
            dueDate = Calendar.getInstance().apply { timeInMillis = 32342 },
            lastReviewDate = Calendar.getInstance().apply { timeInMillis = 32343 },
            lastDuration = 34,
            categoryId = -1
        ),
        Topic(
            id = 4,
            title = "title4",
            dateCreated = Calendar.getInstance().apply { timeInMillis = 42341 },
            dueDate = Calendar.getInstance().apply { timeInMillis = 42342 },
            lastReviewDate = Calendar.getInstance().apply { timeInMillis = 42343 },
            lastDuration = 44,
            categoryId = -1
        ),
        Topic(
            id = 5,
            title = "title5",
            dateCreated = Calendar.getInstance().apply { timeInMillis = 52341 },
            dueDate = Calendar.getInstance().apply { timeInMillis = 52342 },
            lastReviewDate = Calendar.getInstance().apply { timeInMillis = 52343 },
            lastDuration = 54,
            categoryId = -1
        ),
        Topic(
            id = 6,
            title = "title6",
            dateCreated = Calendar.getInstance().apply { timeInMillis = 62341 },
            dueDate = Calendar.getInstance().apply { timeInMillis = 62342 },
            lastReviewDate = Calendar.getInstance().apply { timeInMillis = 62343 },
            lastDuration = 64,
            categoryId = -1
        )
    )

}