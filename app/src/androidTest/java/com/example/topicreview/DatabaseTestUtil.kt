package com.example.topicreview

import com.example.topicreview.database.Topic
import java.util.*

object DatabaseTestUtil {

    val testTopics = listOf(
        Topic(
            id = 1,
            title = "title1",
            dateCreated = getCal(12341),
            dueDate = getCal(12342),
            lastReviewDate = getCal(12343),
            lastDuration = 14,
            categoryId = -1
        ),
        Topic(
            id = 2,
            title = "title2",
            dateCreated = getCal(22341),
            dueDate = getCal(22342),
            lastReviewDate = getCal(22343),
            lastDuration = 24,
            categoryId = -1
        ),
        Topic(
            id = 3,
            title = "title3",
            dateCreated = getCal(32341),
            dueDate = getCal(32342),
            lastReviewDate = getCal(32343),
            lastDuration = 34,
            categoryId = -1
        ),
        Topic(
            id = 4,
            title = "title4",
            dateCreated = getCal(42341),
            dueDate = getCal(42342),
            lastReviewDate = getCal(42343),
            lastDuration = 44,
            categoryId = -1
        ),
        Topic(
            id = 5,
            title = "title5",
            dateCreated = getCal(52341),
            dueDate = getCal(52342),
            lastReviewDate = getCal(52343),
            lastDuration = 54,
            categoryId = -1
        ),
        Topic(
            id = 6,
            title = "title6",
            dateCreated = getCal(62341),
            dueDate = getCal(62342),
            lastReviewDate = getCal(62343),
            lastDuration = 64,
            categoryId = -1
        )
    )

    fun getTopic(
        title: String,
        dd: Calendar = Calendar.getInstance(),
        dc: Calendar = Calendar.getInstance(),
        lrd: Calendar = Calendar.getInstance(),
        ld: Int = 0,
        cid: Int = -1
    ) = Topic(title = title, dateCreated = dc, dueDate = dd, lastReviewDate = lrd, categoryId = cid, lastDuration = ld)

    fun getCal(time: Long): Calendar = Calendar.getInstance().apply { timeInMillis = time }
}