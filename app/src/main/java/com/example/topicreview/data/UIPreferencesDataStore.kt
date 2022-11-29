package com.example.topicreview.data

import UiPreferences
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.homeDataStore: DataStore<UiPreferences.HomePreferences> by dataStore(
    fileName = "ui_preferences.pb",
    serializer = HomePreferencesSerializer
)

class UIPreferencesDataStore(
    private val dataStore: DataStore<UiPreferences.HomePreferences>
) {

    val sorting: Flow<UiPreferences.Sorting> = dataStore.data.map { homePreferences ->
        homePreferences.sorting
    }

    suspend fun setSorting(sort: UiPreferences.Sorting) {
        dataStore.updateData { homePreferences ->
            homePreferences.toBuilder()
                .setSorting(sort)
                .build()
        }
    }
}