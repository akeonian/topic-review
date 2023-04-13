package com.example.topicreview

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.topicreview.database.ReviewDao
import com.example.topicreview.database.ReviewDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
abstract class BaseReviewDatabaseTest: CoroutineTest() {

    protected lateinit var reviewDao: ReviewDao
    private lateinit var reviewDatabase: ReviewDatabase

    @Before
    fun onCreateDB() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        reviewDatabase = Room.inMemoryDatabaseBuilder(
            context, ReviewDatabase::class.java).build()
        reviewDao = reviewDatabase.reviewDao()
    }

    @After
    fun closeDb() {
        reviewDatabase.close()
    }

    protected fun <T> LiveData<T>.getOrAwaitValue(): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object: Observer<T> {
            override fun onChanged(t: T) {
                data = t
                latch.countDown()
                removeObserver(this)
            }
        }
        observeForever(observer)
        if (!latch.await(2, TimeUnit.SECONDS)) {
            removeObserver(observer)
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}