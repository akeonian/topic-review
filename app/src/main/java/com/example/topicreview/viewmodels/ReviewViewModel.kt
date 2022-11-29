package com.example.topicreview.viewmodels

import UiPreferences
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.*
import com.example.topicreview.data.UIPreferencesDataStore
import com.example.topicreview.database.Category
import com.example.topicreview.database.ReviewDao
import com.example.topicreview.database.Sorting
import com.example.topicreview.database.Topic
import com.example.topicreview.utils.DatabaseUtils
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class ReviewViewModel(
    private val reviewDao: ReviewDao,
    private val uiPreferencesDataStore: UIPreferencesDataStore
): ViewModel() {

    private val _sorting = uiPreferencesDataStore.sorting
    val sorting: LiveData<UiPreferences.Sorting> = _sorting.asLiveData()

    val latestDueTopics: LiveData<List<Topic>> get() {
        val s = when(sorting.value) {
            UiPreferences.Sorting.Z_TO_A -> Sorting.Z_TO_A
            else -> Sorting.A_TO_Z
        }
        val time = Calendar.getInstance().timeInMillis
        return reviewDao.getDueTopics(time, s).asLiveData()
    }
    val allTopic: LiveData<List<Topic>> = reviewDao.getTopics().asLiveData()

    fun isValidTopicInput(title: String): Boolean {
        return title.isNotBlank()
    }

    fun isValidCategoryInput(title: String): Boolean {
        return title.isNotBlank()
    }

    fun insertTopic(title: String) {
        val today = Calendar.getInstance()
        val newTopic = Topic(
            title = title,
            dateCreated = today,
            dueDate = today,
            lastReviewDate = today
        )
        viewModelScope.launch {
            reviewDao.insertTopic(newTopic)
        }
    }

    fun delete(topic: Topic) {
        viewModelScope.launch {
            reviewDao.deleteTopic(topic)
        }
    }

    fun getTopic(id: Int): LiveData<Topic> {
        return reviewDao.getTopic(id).asLiveData()
    }

    fun editTopic(topic: Topic, title: String) {
        viewModelScope.launch {
            val editedTopic = topic.copy(
                title = title,
                dateCreated = topic.dateCreated,
                dueDate = topic.dueDate
            )
            reviewDao.updateTopic(editedTopic)
        }
    }

    fun updateTopicReviewDate(topic: Topic, noOfDays: Int) {
        viewModelScope.launch {
            val reviewTopics = getNextDueDate(topic, noOfDays)
            reviewDao.updateTopic(reviewTopics)
        }
    }

    fun insertCategory(title: String) {
        viewModelScope.launch {
            val category = Category(title = title)
            reviewDao.insertCategory(category)
        }
    }

    fun getCategory(categoryId: Int): LiveData<Category> {
        return reviewDao.getCategory(categoryId).distinctUntilChanged().asLiveData()
    }

    fun editCategory(category: Category, title: String) {
        viewModelScope.launch {
            val editedCategory = category.copy(title = title)
            reviewDao.updateCategory(editedCategory)
        }
    }

    fun setSorting(sorting: UiPreferences.Sorting) {
        viewModelScope.launch {
            uiPreferencesDataStore.setSorting(sorting)
        }
    }

    fun importData(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    DatabaseUtils.importDataFromCSV(reviewDao, inputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun exportData(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    DatabaseUtils.exportDataToCSV(reviewDao, outputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getNextDueDate(topic: Topic, noOfDays: Int): Topic {
        val reviewDay = Calendar.getInstance()
        reviewDay.add(Calendar.DATE, noOfDays)
        reviewDay.set(Calendar.HOUR_OF_DAY, 0)
        reviewDay.set(Calendar.MINUTE, 0)
        reviewDay.set(Calendar.SECOND, 0)
        reviewDay.set(Calendar.MILLISECOND, 0)
        return topic.copy(dueDate = reviewDay)
    }

    class Factory(
        private val reviewDao: ReviewDao,
        private val dataStore: UIPreferencesDataStore
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewViewModel(reviewDao, dataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}