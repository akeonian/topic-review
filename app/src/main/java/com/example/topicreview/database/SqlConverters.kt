package com.example.topicreview.database

import androidx.room.TypeConverter
import java.util.Calendar

class SqlConverters {
    
    @TypeConverter
    fun toDateCalendar(sqlTime: Long): Calendar {
        val c = Calendar.getInstance()
        c.timeInMillis = sqlTime
        return c
    }

    @TypeConverter
    fun fromCalendar(calendar: Calendar): Long {
        return calendar.timeInMillis
    }
}