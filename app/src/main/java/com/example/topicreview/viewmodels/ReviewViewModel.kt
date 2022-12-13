package com.example.topicreview.viewmodels

import UiPreferences
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.*
import com.example.topicreview.data.UIPreferencesDataStore
import com.example.topicreview.database.*
import com.example.topicreview.models.ReviewCategory
import com.example.topicreview.models.ReviewItem
import com.example.topicreview.models.ReviewTopic
import com.example.topicreview.repository.ReviewRepository
import com.example.topicreview.utils.DatabaseUtils
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class ReviewViewModel(
    private val reviewDao: ReviewDao,
    private val reviewRepository: ReviewRepository,
    private val uiPreferencesDataStore: UIPreferencesDataStore
): ViewModel() {

    private val _sorting = uiPreferencesDataStore.sorting
    val sorting: LiveData<UiPreferences.Sorting> = _sorting.asLiveData()

    val latestReviewItems: LiveData<List<ReviewItem>> get() {
        val s = when(sorting.value) {
            UiPreferences.Sorting.Z_TO_A -> Sorting.Z_TO_A
            else -> Sorting.A_TO_Z
        }
        val time = Calendar.getInstance().timeInMillis

        return reviewRepository.getDueItems(time, s).asLiveData()
    }
    val allItems: LiveData<List<ReviewItem>> = reviewRepository.getAllItems().asLiveData()

    fun delete(topic: ReviewTopic) {
        viewModelScope.launch {
            reviewRepository.deleteTopic(topic)
        }
    }

    fun delete(category: ReviewCategory) {
        viewModelScope.launch {
            reviewRepository.deleteCategory(category)
        }
    }

    fun updateTopicReviewDate(topic: ReviewTopic, noOfDays: Int) {
        viewModelScope.launch {
            reviewRepository.updateTopicReviewDate(topic, noOfDays)
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

    class Factory(
        private val reviewDao: ReviewDao,
        private val reviewRepository: ReviewRepository,
        private val dataStore: UIPreferencesDataStore
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewViewModel(reviewDao, reviewRepository, dataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}