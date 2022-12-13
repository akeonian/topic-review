package com.example.topicreview.viewmodels

import androidx.lifecycle.*
import com.example.topicreview.database.Category
import com.example.topicreview.database.ReviewDao
import com.example.topicreview.database.Topic
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.*

class AddEditViewModel(
    private val reviewDao: ReviewDao): ViewModel() {

    fun isValidTopicInput(title: String): Boolean {
        return title.isNotBlank()
    }

    fun isValidCategoryInput(title: String): Boolean {
        return title.isNotBlank()
    }

    fun insertTopic(title: String, categoryId: Int) {
        val today = Calendar.getInstance()
        val newTopic = Topic(
            title = title,
            dateCreated = today,
            dueDate = today,
            lastReviewDate = today,
            categoryId = categoryId
        )
        viewModelScope.launch {
            reviewDao.insertTopic(newTopic)
        }
    }

    fun getTopic(id: Int): LiveData<Topic> {
        return reviewDao.getTopic(id).asLiveData()
    }

    fun editTopic(topic: Topic, title: String, categoryId: Int) {
        viewModelScope.launch {
            val editedTopic = topic.copy(
                title = title,
                dateCreated = topic.dateCreated,
                dueDate = topic.dueDate,
                categoryId = categoryId
            )
            reviewDao.updateTopic(editedTopic)
        }
    }

    fun insertCategory(title: String, parentId: Int) {
        viewModelScope.launch {
            val category = Category(title = title, parentId = parentId)
            reviewDao.insertCategory(category)
        }
    }

    fun getCategory(categoryId: Int): LiveData<Category> {
        return reviewDao.getCategory(categoryId).distinctUntilChanged().asLiveData()
    }

    fun getCategories(): LiveData<List<Category>> {
        return reviewDao.getCategories().asLiveData()
    }

    fun getCategoriesExcept(categoryId: Int): LiveData<List<Category>> {
        return reviewDao.getCategoriesExcept(categoryId).asLiveData()
    }

    fun editCategory(category: Category, title: String, parentId: Int) {
        viewModelScope.launch {
            val editedCategory = category.copy(
                title = title, parentId = parentId)
            reviewDao.updateCategory(editedCategory)
        }
    }

    class Factory(
        private val reviewDao: ReviewDao
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddEditViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddEditViewModel(reviewDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}