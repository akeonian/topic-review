package com.example.topicreview.viewmodels

import com.example.topicreview.BaseReviewDatabaseTest
import com.example.topicreview.DatabaseTestUtil
import com.example.topicreview.database.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AddEditViewModelTest : BaseReviewDatabaseTest() {

    private lateinit var aeViewModel: AddEditViewModel

    @Before
    fun createAddEditViewModel() {
        aeViewModel = AddEditViewModel(reviewDao)
    }

    @Test
    fun isValidTopicInput() {
        assertFalse(aeViewModel.isValidTopicInput(""))
        assertTrue(aeViewModel.isValidTopicInput("Topic No. 1"))
    }

    @Test
    fun isValidCategoryInput() {
        assertFalse(aeViewModel.isValidCategoryInput(""))
        assertTrue(aeViewModel.isValidCategoryInput("Some Category"))
    }

    @Test
    fun insertTopic() = coTest {
        aeViewModel.insertTopic("Topic 1", 132)
        val topics = reviewDao.getTopics().first()
        var wasAdded = false
        println("topic=$topics")
        for (topic in topics) {
            if (topic.title == "Topic 1" && topic.categoryId == 132) {
                wasAdded = true
                break
            }
        }
        assertTrue(wasAdded)
    }

    @Test
    fun getTopic() = coTest {
        val data = DatabaseTestUtil.getTopic("A topic")
        val id = reviewDao.insertTopic(data).toInt()
        val topic = aeViewModel.getTopic(id).getOrAwaitValue()
        assertEquals(data.copy(id = id), topic)
    }

    @Test
    fun editTopic() = coTest {
        val data = DatabaseTestUtil.getTopic("A topic")
        val id = reviewDao.insertTopic(data).toInt()
        val topic = data.copy(id = id)
        aeViewModel.editTopic(topic, "New Topic", 123)
        val updated = reviewDao.getTopic(id).first()
        assertEquals("New Topic", updated.title)
        assertEquals(123, updated.categoryId)
    }

    @Test
    fun insertCategory() = coTest {
        aeViewModel.insertCategory("New Category", 45)
        val categories = reviewDao.getCategories().first()
        var wasAdded = false
        println("topic=$categories")
        for (category in categories) {
            if (category.title == "New Category" && category.parentId == 45) {
                wasAdded = true
                break
            }
        }
        assertTrue(wasAdded)
    }

    @Test
    fun getCategory() = coTest {
        val data = Category(title = "Category 1")
        val id = reviewDao.insertCategory(data).toInt()
        val cat = aeViewModel.getCategory(id).getOrAwaitValue()
        assertEquals(data.copy(id = id), cat)
    }

    @Test
    fun getCategories() = coTest {
        val data = listOf(
            Category(title = "Category 1"),
            Category(title = "Category 2"),
            Category(title = "Category 3"),
            Category(title = "Category 4"),
            Category(title = "Category 5")
        )
        val idSet = mutableSetOf<Int>()
        for (cat in data) {
            val id = reviewDao.insertCategory(cat).toInt()
            idSet.add(id)
        }
        val cats = aeViewModel.getCategories().getOrAwaitValue()
        assertEquals(idSet.size, cats.size)
        var has = true
        for (cat in cats) {
            if (!idSet.contains(cat.id)) {
                has = false
                break
            }
        }
        assertTrue(has)
    }

    @Test
    fun getCategoriesExcept() = coTest {
        val data = listOf(
            Category(title = "Category 1"),
            Category(title = "Category 2"),
            Category(title = "Category 3"),
            Category(title = "Category 4"),
            Category(title = "Category 5")
        )
        val idSet = mutableSetOf<Int>()
        for (cat in data) {
            val id = reviewDao.insertCategory(cat).toInt()
            idSet.add(id)
        }
        val except = idSet.random()
        idSet.remove(except)
        val cats = aeViewModel.getCategoriesExcept(except).getOrAwaitValue()
        assertEquals(idSet.size, cats.size)
        var excluded = true
        var hasInvalid = false
        for (cat in cats) {
            if (cat.id == except) {
                excluded = false
            } else if (!idSet.contains(cat.id)) {
                hasInvalid = true
                break
            }
        }
        assertTrue(excluded)
        assertFalse(hasInvalid)
    }

    @Test
    fun editCategory() = coTest {
        var data = Category(title = "Old Title", parentId = 8)
        val id = reviewDao.insertCategory(data).toInt()
        data = data.copy(id = id)
        aeViewModel.editCategory(data, "New Title", 34)
        val updated = reviewDao.getCategory(id).first()
        data = data.copy(title = "New Title", parentId = 34)
        assertEquals(data, updated)
    }
}