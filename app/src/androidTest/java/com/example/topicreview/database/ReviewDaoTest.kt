package com.example.topicreview.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.topicreview.BaseReviewDatabaseTest
import com.example.topicreview.DatabaseTestUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ReviewDaoTest: BaseReviewDatabaseTest() {

    @Test
    fun testDueTopicSorting() = runTest {
        val testData = listOf(
            DatabaseTestUtil.getTopic("A Topic"),
            DatabaseTestUtil.getTopic("C Topic"),
            DatabaseTestUtil.getTopic("B Topic")
        )
        reviewDao.insertTopics(testData)
        val asc = reviewDao.getDueTopics(
            Calendar.getInstance().timeInMillis, Sorting.A_TO_Z).first()
        assertEquals("A Topic", asc[0].title)
        assertEquals("B Topic", asc[1].title)
        assertEquals("C Topic", asc[2].title)

        val desc = reviewDao.getDueTopics(
            Calendar.getInstance().timeInMillis, Sorting.Z_TO_A).first()
        assertEquals("C Topic", desc[0].title)
        assertEquals("B Topic", desc[1].title)
        assertEquals("A Topic", desc[2].title)
    }

    @Test
    fun testDueTopics() = runTest {
        val testDueTime = 23143L
        val testData = listOf(
            DatabaseTestUtil.getTopic("Before Topic", DatabaseTestUtil.getCal(testDueTime - 20)),
            DatabaseTestUtil.getTopic("After Topic", DatabaseTestUtil.getCal(testDueTime + 20)),
            DatabaseTestUtil.getTopic("Exact Topic", DatabaseTestUtil.getCal(testDueTime))
        )
        reviewDao.insertTopics(testData)
        val asc = reviewDao.getDueTopics(
            DatabaseTestUtil.getCal(testDueTime).timeInMillis, Sorting.A_TO_Z).first()
        assertEquals("Before Topic", asc[0].title)
        assertEquals("Exact Topic", asc[1].title)
    }

}