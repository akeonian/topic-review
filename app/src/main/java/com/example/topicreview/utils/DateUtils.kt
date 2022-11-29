package com.example.topicreview.utils

import java.util.*

fun Calendar.getDaysFrom(cal: Calendar = Calendar.getInstance()): Int {
    return getDaysBetweenMillis(timeInMillis, cal.timeInMillis)
}

fun getDaysBetweenMillis(after: Long, before: Long): Int {
    // day of year1 = 132, 2002
    // day of year2 =   1, 2003
    // diff = 365 - 132 + 1
    val startCalendar = Calendar.getInstance().apply {
        timeInMillis = if (after > before) before else after
    }
    val endCalendar = Calendar.getInstance().apply {
        timeInMillis = if (after > before) after else before
    }
    val endYear = endCalendar.get(Calendar.YEAR)
    var startYear = startCalendar.get(Calendar.YEAR)
    var endDay = endCalendar.get(Calendar.DAY_OF_YEAR)
    val startDay = startCalendar.get(Calendar.DAY_OF_YEAR)
    while (startYear < endYear) {
        endDay += startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)
        startYear++
        startCalendar.set(Calendar.YEAR, startYear)
    }
    return if (after > before)
        endDay - startDay
    else
        startDay - endDay
}