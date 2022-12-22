package com.example.topicreview.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.topicreview.BaseReviewDatabaseTest
import com.example.topicreview.DatabaseTestUtil
import com.example.topicreview.database.Topic
import com.example.topicreview.utils.DatabaseUtils.exportDataToCSV
import com.example.topicreview.utils.DatabaseUtils.importDataFromCSV
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseUtilsTest: BaseReviewDatabaseTest() {

    @Test
    fun importDataFromCSVSample1() {
        val testData = DatabaseTestUtil.testTopics
        val textData = getTestImportText1(testData)
        testImport(testData, textData)
    }

    @Test
    fun importDataFromCSVSample2() {
        val testData = DatabaseTestUtil.testTopics
        val textData = getTestImportText2(testData)
        testImport(testData, textData)
    }

    private fun testImport(testData: List<Topic>, textData: String) {
        val inputStream = textData.byteInputStream(StandardCharsets.UTF_8)
        runBlocking { importDataFromCSV(reviewDao, inputStream) }
        var dataList: List<Topic>
        runBlocking { dataList = reviewDao.getTopics().first() }
        assertEquals(6, dataList.size)
        var notFound = false
        for (data in dataList) {
            var found = false
            for (t in testData) {
                if (t.title == data.title &&
                    t.dateCreated.timeInMillis == data.dateCreated.timeInMillis &&
                    t.dueDate.timeInMillis == data.dueDate.timeInMillis &&
                    t.lastReviewDate.timeInMillis == data.lastReviewDate.timeInMillis &&
                    t.lastDuration == data.lastDuration &&
                    t.categoryId == data.categoryId) {
                    found = true
                }
            }
            if (!found) {
                notFound = true
                break
            }
        }

        assertFalse(notFound)
    }

    @Test
    fun exportDataFromCSVSuccess() {
        val testData = DatabaseTestUtil.testTopics
        runBlocking { reviewDao.insertTopics(testData) }
        val outputStream = ByteArrayOutputStream()
        runBlocking { exportDataToCSV(reviewDao, outputStream) }
        val outputData = outputStream.toString("UTF-8")
        val expectedData = getTestImportText1(testData)
        assertEquals(expectedData, outputData)
    }

    // Using auto generated sample because it just uses concatenation
    // in a loop
    private fun getTestImportText1(topics: List<Topic>): String {
        val data = StringBuilder("_id;title;date_created;due_date;last_review_date;last_review_duration;category_id")
        for (topic in topics) {
            data.append("\n${topic.id};${topic.title};${topic.dateCreated.timeInMillis};${topic.dueDate.timeInMillis};${topic.lastReviewDate.timeInMillis};${topic.lastDuration};${topic.categoryId}")
        }
        return data.toString()
    }

    private fun getTestImportText2(topics: List<Topic>): String {
        val data = StringBuilder("_id;date_created;title;due_date;last_review_duration;category_id;last_review_date")
        for (topic in topics) {
            data.append("\n${topic.id};${topic.dateCreated.timeInMillis};${topic.title};${topic.dueDate.timeInMillis};${topic.lastDuration};${topic.categoryId};${topic.lastReviewDate.timeInMillis}")
        }
        return data.toString()
    }
}