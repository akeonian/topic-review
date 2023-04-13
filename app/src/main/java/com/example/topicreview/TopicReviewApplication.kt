package com.example.topicreview

import androidx.multidex.MultiDexApplication
import com.example.topicreview.database.ReviewDatabase

class TopicReviewApplication: MultiDexApplication() {

    val database: ReviewDatabase by lazy { ReviewDatabase.getDatabase(this) }

}