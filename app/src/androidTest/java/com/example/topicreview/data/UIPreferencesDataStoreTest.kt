package com.example.topicreview.data

import UiPreferences
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UIPreferencesDataStoreTest {

    // Test Rule that swaps the background executor used by the
    // Architecture Components with a different one which
    // executes each task synchronously
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var ds: DataStore<UiPreferences.HomePreferences>

    @Before
    fun createDatastore() {
        ds = InstrumentationRegistry.getInstrumentation().targetContext.homeDataStore
    }

    @After
    fun removeDataStore() {
        File(
            ApplicationProvider.getApplicationContext<Context>().filesDir,
            "datastore"
        ).deleteRecursively()
    }

    @Test
    fun checkSorting() = runTest {
        val uiStore = UIPreferencesDataStore(ds)
        // Default value
        assertEquals(UiPreferences.Sorting.A_TO_Z, uiStore.sorting.first())
        uiStore.setSorting(UiPreferences.Sorting.Z_TO_A)
        assertEquals(UiPreferences.Sorting.Z_TO_A, uiStore.sorting.first())
        uiStore.setSorting(UiPreferences.Sorting.A_TO_Z)
        assertEquals(UiPreferences.Sorting.A_TO_Z, uiStore.sorting.first())
    }
}