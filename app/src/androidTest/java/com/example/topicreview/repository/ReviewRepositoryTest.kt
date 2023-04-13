package com.example.topicreview.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.topicreview.BaseReviewDatabaseTest
import com.example.topicreview.DatabaseTestUtil
import com.example.topicreview.database.Category
import com.example.topicreview.database.Sorting
import com.example.topicreview.database.asReviewTopic
import com.example.topicreview.models.ReviewCategory
import com.example.topicreview.models.ReviewTopic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ReviewRepositoryTest : BaseReviewDatabaseTest() {

    private lateinit var reviewRepository: ReviewRepository

    @Before
    fun createRepository() {
        reviewRepository = ReviewRepository(reviewDao)
    }

    @Test
    fun getAllItems() = runTest {
        // Test Data Representation
        // - Category 1,
        //      - Topic 1
        //      - Category 2
        //            - Category 3
        //            - Topic 2
        // - Topic 3
        val c1 = Category(title = "Category 1")
        val c1Id = reviewDao.insertCategory(c1).toInt()
        val t1 = DatabaseTestUtil.getTopic("Topic 1", cid = c1Id)
        val t1Id = reviewDao.insertTopic(t1).toInt()
        val c2 = Category(title = "Category 2", parentId = c1Id)
        val c2Id = reviewDao.insertCategory(c2).toInt()
        val c3 = Category(title = "Category 3", parentId = c2Id)
        val c3Id = reviewDao.insertCategory(c3).toInt()
        val t2 = DatabaseTestUtil.getTopic("Topic 2", cid = c2Id)
        val t2Id = reviewDao.insertTopic(t2).toInt()
        val t3 = DatabaseTestUtil.getTopic("Topic 3")
        val t3Id = reviewDao.insertTopic(t3).toInt()

        val allItems = reviewRepository.getAllItems().first()
        assertEquals(2, allItems.size)
        // A ReviewTopic and a ReviewCategory can have same ids
        for (item in allItems) {
            if (item is ReviewTopic) {
                assertEquals(t3Id,  item.id)
            }
            else if (item is ReviewCategory) {
                assertEquals(c1Id,  item.id)
                assertEquals(2, item.subItems.size)
                for (s1 in item.subItems) {
                    if (s1 is ReviewTopic) assertEquals(t1Id, s1.id)
                    if (s1 is ReviewCategory) {
                        assertEquals(c2Id, s1.id)
                        assertEquals(2, item.subItems.size)
                        for (s2 in s1.subItems) {
                            if (s2 is ReviewTopic) assertEquals(t2Id, s2.id)
                            if (s2 is ReviewCategory) {
                                assertEquals(c3Id, s2.id)
                                assertEquals(0, s2.subItems.size)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun getDueItems() = runTest {
        // Test Data Representation
        // - Category 1,
        //      - Topic 1 // Due
        //      - Category 2
        //            - Category 3
        //            - Topic 2 // Not Due
        // - Topic 3 // Due
        // - Topic 4 // Not Due
        // - Topic 5 // Current Time
        val ct = 1234L // Current Time
        val c1 = Category(title = "Category 1")
        val c1Id = reviewDao.insertCategory(c1).toInt()
        val t1 = DatabaseTestUtil.getTopic("Topic 1", cid = c1Id, dd = DatabaseTestUtil.getCal(ct - 20L))
        val t1Id = reviewDao.insertTopic(t1).toInt()
        val c2 = Category(title = "Category 2", parentId = c1Id)
        val c2Id = reviewDao.insertCategory(c2).toInt()
        val c3 = Category(title = "Category 3", parentId = c2Id)
        val c3Id = reviewDao.insertCategory(c3).toInt()
        val t2 = DatabaseTestUtil.getTopic("Topic 2", cid = c2Id, dd = DatabaseTestUtil.getCal(ct + 20L))
        reviewDao.insertTopic(t2).toInt()
        val t3 = DatabaseTestUtil.getTopic("Topic 3", dd = DatabaseTestUtil.getCal(ct - 20L))
        val t3Id = reviewDao.insertTopic(t3).toInt()
        val t4 = DatabaseTestUtil.getTopic("Topic 4", dd = DatabaseTestUtil.getCal(ct + 20L))
        reviewDao.insertTopic(t4).toInt()
        val t5 = DatabaseTestUtil.getTopic("Topic 5", dd = DatabaseTestUtil.getCal(ct))
        val t5Id = reviewDao.insertTopic(t5).toInt()

        // Result should be
        // - Category 1,
        //      - Topic 1 // Due
        //      - Category 2
        //            - Category 3
        // - Topic 3 // Due
        // - Topic 5 // Current Time
        // TODO: Change Test to test for sorting, current implementation does not sort
        val dueItems = reviewRepository.getDueItems(ct, Sorting.A_TO_Z).first()
        assertEquals(3, dueItems.size)
        // A ReviewTopic and a ReviewCategory can have same ids
        for (item in dueItems) {
            if (item is ReviewTopic) {
                if (item.id == t3Id) assertEquals("Topic 3", item.title)
                if (item.id == t5Id) assertEquals("Topic 5", item.title)
            }
            else if (item is ReviewCategory) {
                assertEquals(c1Id,  item.id)
                assertEquals(2, item.subItems.size)
                for (s1 in item.subItems) {
                    if (s1 is ReviewTopic) assertEquals(t1Id, s1.id)
                    if (s1 is ReviewCategory) {
                        assertEquals(c2Id, s1.id)
                        assertEquals(1, s1.subItems.size)
                        // one category
                        val cat3 = s1.subItems[0] as ReviewCategory
                        assertEquals(c3Id, cat3.id)
                        assertEquals(0, cat3.subItems.size)
                    }
                }
            }
        }
    }

    @Test
    fun deleteTopic() = runTest {
        val t1 = DatabaseTestUtil.getTopic("Topic 1")
        val t1Id = reviewDao.insertTopic(t1).toInt()
        reviewRepository.deleteTopic(t1.copy(id = t1Id).asReviewTopic())
        val f = reviewDao.getTopic(t1Id)
        assertNull(f.firstOrNull())
    }

    @Test
    fun updateTopicReviewDate() = runTest {

        val insertDay = DatabaseTestUtil.getCal(3838381)
        val t1 = DatabaseTestUtil.getTopic("Topic 1", dd = insertDay)
        val t1Id = reviewDao.insertTopic(t1).toInt()
        // Before reviewing, one due on the day of insert
        var dueItems = reviewRepository.getDueItems(insertDay.timeInMillis, Sorting.A_TO_Z).first()
        assertEquals(1, dueItems.size)

        reviewRepository.updateTopicReviewDate(t1.copy(id = t1Id).asReviewTopic(), 4)
        // After reviewing, no dues on the day of insert
        dueItems = reviewRepository.getDueItems(insertDay.timeInMillis, Sorting.A_TO_Z).first()
        assertEquals(0, dueItems.size)

        val after4Days = Calendar.getInstance().run {
            add(Calendar.DATE, 4)
            timeInMillis
        }
        dueItems = reviewRepository.getDueItems(after4Days, Sorting.A_TO_Z).first()
        val topic = dueItems[0] as ReviewTopic
        assertEquals("Topic 1", topic.title)
    }
}