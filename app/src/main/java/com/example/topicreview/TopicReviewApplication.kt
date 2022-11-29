package com.example.topicreview

import android.app.Application
import com.example.topicreview.database.ReviewDatabase

class TopicReviewApplication: Application() {

    val database: ReviewDatabase by lazy { ReviewDatabase.getDatabase(this) }

}