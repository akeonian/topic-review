package com.example.topicreview.utils

import com.example.topicreview.database.ReviewDao
import com.example.topicreview.database.Topic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "DatabaseUtils"

object DatabaseUtils {
    private val KEY_ID = "_id"
    private val KEY_TITLE = "title"
    private val KEY_DUE_DATE = "due_date"
    private val KEY_DATE_CREATED = "date_created"
    private val KEY_LAST_REVIEW_DATE = "last_review_date"
    private val KEY_LAST_REVIEW_DURATION = "last_review_duration"
    private val KEY_CATEGORY_ID = "category_id"
    private val DELIMITER = ";"

    private class TopicConverter(line: String, lineNumber: Int) {
        val title: Int
        val dateCreated: Int
        val dueDate: Int
        val lastReviewDate: Int
        val lastReviewDuration: Int

        init {
            val attrs = line.split(DELIMITER)
            val attrMap = mutableMapOf<String, Int>()
            for (i in attrs.indices) {
                attrMap[attrs[i]] = i
            }
            title = attrMap[KEY_TITLE] ?: throw IllegalFileFormatException("attribute title is not defined", lineNumber)
            dateCreated = attrMap[KEY_DATE_CREATED] ?: throw IllegalFileFormatException("attribute date_created is not defined", lineNumber)
            dueDate = attrMap[KEY_DUE_DATE] ?: throw IllegalFileFormatException("attribute due_date is not defined", lineNumber)
            lastReviewDate = attrMap[KEY_LAST_REVIEW_DATE] ?: throw IllegalFileFormatException("attribute last_review_date is not defined", lineNumber)
            lastReviewDuration = attrMap[KEY_LAST_REVIEW_DURATION] ?: throw IllegalFileFormatException("attribute last_review_duration is not defined", lineNumber)
        }

        fun getTopic(line: String, lineNumber: Int): Topic {
            try {
                val values = line.split(";")
                return Topic(
                    title = values[title].toTitle(lineNumber),
                    dateCreated = values[dateCreated].toCalendar(lineNumber),
                    dueDate = values[dateCreated].toCalendar(lineNumber),
                    lastReviewDate = values[lastReviewDate].toCalendar(lineNumber),
                    lastDuration = values[lastReviewDuration].toDays(lineNumber)
                )
            } catch (e: IndexOutOfBoundsException) {
                throw IllegalFileFormatException("All attributes of [title, date_created, due_date, last_review_date, last_review_duration] are not defined", lineNumber)
            }
        }

        fun String.toTitle(lineNumber: Int): String {
            if (isEmpty()) throw IllegalFileFormatException("Title of the record cannot be empty", lineNumber)
            return this
        }

        fun String.toCalendar(lineNumber: Int): Calendar {
            val cal = Calendar.getInstance()
            try {
                cal.timeInMillis = toLong()
            } catch (e: NumberFormatException) {
                throw IllegalFileFormatException("Could not convert $this to number", lineNumber)
            }
            return cal
        }

        fun String.toDays(lineNumber: Int): Int {
            try {
                return TimeUnit.MICROSECONDS.toDays(toLong()).toInt()
            } catch (e: NumberFormatException) {
                throw IllegalFileFormatException("Could not convert $this to number of days", lineNumber)
            }
        }
    }

    suspend fun importDataFromCSV(reviewDao: ReviewDao, inputStream: InputStream) {
        withContext(Dispatchers.IO) {
            try {
                inputStream.bufferedReader().use { br ->
                    var line: String? = br.readLine()
                    var lineNumber = 1
                    if (!line.isNullOrEmpty()) {
                        val records = mutableListOf<Topic>()
                        val converter = TopicConverter(line, lineNumber)
                        line = br.readLine()
                        lineNumber +=1
                        while (!line.isNullOrEmpty()) {
                            val topic = converter.getTopic(line, lineNumber)
                            records.add(topic)
                            line = br.readLine()
                            lineNumber +=1
                        }

                        reviewDao.insertTopics(records)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalFileFormatException) {
                e.printStackTrace()
            }
        }
    }

    suspend fun exportDataToCSV(reviewDao: ReviewDao, outputStream: OutputStream) {
        withContext(Dispatchers.IO) {
            val topics = reviewDao.getTopics().first()
            try {
                outputStream.bufferedWriter().use { bw ->
                    val sb = StringBuilder()
                    sb.append(KEY_ID).append(DELIMITER)
                        .append(KEY_TITLE).append(DELIMITER)
                        .append(KEY_DATE_CREATED).append(DELIMITER)
                        .append(KEY_DUE_DATE).append(DELIMITER)
                        .append(KEY_LAST_REVIEW_DATE).append(DELIMITER)
                        .append(KEY_LAST_REVIEW_DURATION).append(DELIMITER)
                        .append(KEY_CATEGORY_ID)
                    bw.write(sb.toString())
                    for (topic in topics) {
                        sb.clear()
                        sb.append(topic.id.toString()).append(DELIMITER)
                            .append(topic.title).append(DELIMITER)
                            .append(topic.dateCreated.timeInMillis.toString()).append(DELIMITER)
                            .append(topic.dueDate.timeInMillis.toString()).append(DELIMITER)
                            .append(topic.lastReviewDate.timeInMillis.toString()).append(DELIMITER)
                            .append(topic.lastDuration.toString()).append(DELIMITER)
                            .append(topic.categoryId.toString())
                        bw.newLine()
                        bw.write(sb.toString())
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private class IllegalFileFormatException(message: String, private val lineNumber: Int): Exception(message) {
        override val message: String
            get() = "${super.message}, at line number=$lineNumber"
    }
}