package com.example.topicreview.viewmodels

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.example.topicreview.BaseReviewDatabaseTest
import com.example.topicreview.DatabaseTestUtil
import com.example.topicreview.data.UIPreferencesDataStore
import com.example.topicreview.data.homeDataStore
import com.example.topicreview.database.Category
import com.example.topicreview.database.Sorting
import com.example.topicreview.database.asReviewTopic
import com.example.topicreview.models.ReviewCategory
import com.example.topicreview.repository.ReviewRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.*

@ExperimentalCoroutinesApi
class ReviewViewModelTest : BaseReviewDatabaseTest() {

    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var uiPreferencesDataStore: UIPreferencesDataStore

    @Before
    fun createReviewViewModel() {
        val dataStore = InstrumentationRegistry.getInstrumentation()
            .targetContext.homeDataStore

        reviewRepository = ReviewRepository(reviewDao)
        uiPreferencesDataStore = UIPreferencesDataStore(dataStore)
        reviewViewModel = ReviewViewModel(reviewDao, reviewRepository, uiPreferencesDataStore)
    }

    @After
    fun removeDataStore() {
        File(
            ApplicationProvider.getApplicationContext<Context>().filesDir,
            "datastore"
        ).deleteRecursively()
    }

    @Test
    fun getLatestReviewItems() = coTest {
        // TODO: change test for sorting
        // Data Representation
        // - Topic 1
        // - Topic 2
        // - Topic 3
        // - Topic 4
        // - Topic 5
        val beforeTime = System.currentTimeMillis() - 100
        val afterTime = System.currentTimeMillis() + 1000*60*60*24*2
        val data = listOf(
            DatabaseTestUtil.getTopic("Topic 1", dd = DatabaseTestUtil.getCal(beforeTime)),
            DatabaseTestUtil.getTopic("Topic 2", dd = DatabaseTestUtil.getCal(afterTime)),
            DatabaseTestUtil.getTopic("Topic 3", dd = DatabaseTestUtil.getCal(beforeTime)),
            DatabaseTestUtil.getTopic("Topic 4", dd = DatabaseTestUtil.getCal(beforeTime)),
            DatabaseTestUtil.getTopic("Topic 5", dd = DatabaseTestUtil.getCal(afterTime)),
        )
        reviewDao.insertTopics(data)
        val items = reviewViewModel.latestReviewItems.getOrAwaitValue()
        assertEquals(3,items.size)
        val allExists = items.fold(true) { acc, item ->
            acc && when (item.title) {
                "Topic 1", "Topic 3", "Topic 4" -> true
                else -> false
            }
        }
        assertTrue(allExists)
    }

    @Test
    fun getAllItems() = coTest {
        val data = listOf(
            DatabaseTestUtil.getTopic("Topic 1", dd = DatabaseTestUtil.getCal(134)),
            DatabaseTestUtil.getTopic("Topic 2", dd = DatabaseTestUtil.getCal(12341)),
            DatabaseTestUtil.getTopic("Topic 3", dd = DatabaseTestUtil.getCal(13214)),
            DatabaseTestUtil.getTopic("Topic 4", dd = DatabaseTestUtil.getCal(32412)),
            DatabaseTestUtil.getTopic("Topic 5", dd = DatabaseTestUtil.getCal(143214)),
        )
        reviewDao.insertTopics(data)
        val items = reviewViewModel.allItems.getOrAwaitValue()
        assertEquals(5,items.size)
        val allExists = items.fold(true) { acc, item ->
            acc && data.any { it.title == item.title }
        }
        assertTrue(allExists)

    }

    @Test
    fun deleteTopic() = coTest {
        var testData = DatabaseTestUtil.getTopic("Topic 1", dd = DatabaseTestUtil.getCal(134))
        val id = reviewDao.insertTopic(testData).toInt()
        testData = testData.copy(id = id)
        reviewViewModel.delete(testData.asReviewTopic())
        val data = reviewDao.getTopic(id).firstOrNull()
        assertNull(data)
    }

    @Test
    fun deleteCategory() = coTest {
        val testData = Category(title = "Category 1")
        val id = reviewDao.insertCategory(testData).toInt()
        reviewViewModel.delete(ReviewCategory(
            id = id, // only id needs to match
            title = testData.title,
            daysLeft = 0,
            subItems = emptyList()
        ))
        val data = reviewDao.getCategory(id).firstOrNull()
        assertNull(data)
    }

    @Test
    fun updateTopicReviewDate() = coTest {
        val data = DatabaseTestUtil.getTopic(
            "Topic Test",
            dd = DatabaseTestUtil.getCal(1341)
        )
        val id = reviewDao.insertTopic(data).toInt()
        reviewViewModel.updateTopicReviewDate(data.copy(id = id).asReviewTopic(), 400)
        val beforeDate = Calendar.getInstance().run {
            add(Calendar.DATE, 399)
            timeInMillis
        }
        val before = reviewDao.getDueTopics(beforeDate, Sorting.A_TO_Z).first()
        assertTrue(before.isEmpty())
        val onDate = Calendar.getInstance().run {
            add(Calendar.DATE, 400)
            timeInMillis
        }
        val data400 = reviewDao.getDueTopics(onDate, Sorting.A_TO_Z).first()
        assertEquals(1, data400.size)
        assertEquals("Topic Test", data400[0].title)
    }

}