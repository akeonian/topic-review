package com.example.topicreview.repository

import android.util.Log
import com.example.topicreview.database.*
import com.example.topicreview.models.ReviewCategory
import com.example.topicreview.models.ReviewItem
import com.example.topicreview.models.ReviewTopic
import com.example.topicreview.models.toTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.zip
import java.util.*

private const val TAG = "ReviewRepository"

class ReviewRepository(private val reviewDao: ReviewDao) {

    fun getAllItems(): Flow<List<ReviewItem>> {
        return reviewDao.getCategories().combine(reviewDao.getTopics()) { categories, topics ->
            combineReviewItems(categories, topics)
        }
    }

    fun getDueItems(currentDate: Long, s: Sorting): Flow<List<ReviewItem>> {
        Log.d(TAG, "Called getDueItems")
        return reviewDao.getCategories().zip(reviewDao.getDueTopics(currentDate, s)) { categories, topics ->
            combineReviewItems(categories, topics)
        }
    }

    private fun combineReviewItems(categories: List<Category>, topics: List<Topic>): List<ReviewItem> {
        val reviewItems = mutableListOf<ReviewItem>()
        val categoryById = mutableMapOf<Int, CategoryTemp>()
        for (c in categories) {
            categoryById[c.id] = CategoryTemp(c)
        }
        for (t in topics) {
            if (t.categoryId == -1) {
                val item = t.asReviewTopic()
                reviewItems.add(item)
            } else {
                categoryById[t.categoryId]?.let {
                    val item = t.asReviewTopic()
                    if (it.minTopicDue == -1 || item.daysLeft < it.minTopicDue) {
                        it.minTopicDue = item.daysLeft
                    }
                    it.topics.add(item)
                }
            }
        }
        val rootCategories = mutableListOf<CategoryTemp>()
        for (c in categoryById) {
            val parentId = c.value.category.parentId
            if (parentId == -1) {
                rootCategories.add(c.value)
            } else {
                categoryById[parentId]?.apply {
                    sub.add(c.value)
                }
            }
        }
        reviewItems.addAll(rootCategories.toReviewCategoryList())
        return reviewItems
    }

    suspend fun deleteTopic(topic: ReviewTopic) {
        reviewDao.deleteTopic(topic.toTopic())
    }

    suspend fun deleteCategory(category: ReviewCategory) {
        reviewDao.deleteCategory(category.id)
    }

    suspend fun updateTopicReviewDate(topic: ReviewTopic, noOfDays: Int) {
        val updatedTopic = getNextDueDate(topic, noOfDays)
        reviewDao.updateTopic(updatedTopic)
    }

    private fun getNextDueDate(reviewTopic: ReviewTopic, noOfDays: Int): Topic {
        val reviewDay = Calendar.getInstance()
        reviewDay.add(Calendar.DATE, noOfDays)
        reviewDay.set(Calendar.HOUR_OF_DAY, 0)
        reviewDay.set(Calendar.MINUTE, 0)
        reviewDay.set(Calendar.SECOND, 0)
        reviewDay.set(Calendar.MILLISECOND, 0)
        return reviewTopic.toTopic(dueDate = reviewDay)
    }

    private data class CategoryTemp (
        val category: Category,
        var minTopicDue: Int = -1,
        val sub: MutableList<CategoryTemp> = mutableListOf(),
        val topics: MutableList<ReviewTopic> = mutableListOf()
    ) {

        fun getDaysLeft(): Int {
            var daysLeft = minTopicDue
            for (cat in sub) {
                val d = cat.getDaysLeft()
                if (daysLeft == -1 && d != -1 && d < daysLeft) {
                    daysLeft = d
                }
            }
            return if (daysLeft == -1) 0 else daysLeft
        }
        override fun toString(): String {
            return "CategoryTemp(category=$category, minTopicDue=$minTopicDue, sub=$sub, topics=$topics)"
        }
    }

    private fun CategoryTemp.toReviewCategory(): ReviewCategory {
        val subItems = mutableListOf<ReviewItem>()
        subItems.addAll(topics)
        subItems.addAll(sub.toReviewCategoryList())
        return ReviewCategory(
            id = category.id,
            title = category.title,
            daysLeft = getDaysLeft(),
            subItems = subItems,
        )
    }

    private fun List<CategoryTemp>.toReviewCategoryList(): List<ReviewCategory> {
        return map { it.toReviewCategory() }
    }

}