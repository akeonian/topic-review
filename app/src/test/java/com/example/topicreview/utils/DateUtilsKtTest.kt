package com.example.topicreview.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

internal class DateUtilsKtTest {

    @Test
    fun getDaysBetweenMillisTest() {
        assertEquals(0, getDaysBetweenMillis(0,0))
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.set(22, 0, 10)
        cal2.set(22, 0, 10)
        assertEquals(0, getDaysBetweenMillis(
            cal1.timeInMillis,
            cal2.timeInMillis
        ))

        cal1.set(22, 0, 12)
        cal2.set(22, 0, 10)
        assertEquals(2, getDaysBetweenMillis(
            cal1.timeInMillis,
            cal2.timeInMillis
        ))

        cal1.set(22, 1, 1)
        cal2.set(22, 0, 1)
        assertEquals(31, getDaysBetweenMillis(
            cal1.timeInMillis,
            cal2.timeInMillis
        ))
        cal1.set(22, 2, 1)
        cal2.set(22, 1, 1)
        assertEquals(28, getDaysBetweenMillis(
            cal1.timeInMillis,
            cal2.timeInMillis
        ))
        cal1.set(22, 4, 1)
        cal2.set(22, 3, 1)
        assertEquals(30, getDaysBetweenMillis(
            cal1.timeInMillis,
            cal2.timeInMillis
        ))
        //Leap year
        cal1.set(2016, 2, 1)
        cal2.set(2016, 1, 1)
        assertEquals(29, getDaysBetweenMillis(
            cal1.timeInMillis,
            cal2.timeInMillis
        ))

        cal1.set(23, 1, 10)
        cal2.set(22, 1, 10)
        assertEquals(365, getDaysBetweenMillis(
            cal1.timeInMillis,
            cal2.timeInMillis
        ))
        cal1.set(2017, 1, 10)
        cal2.set(2016, 1, 10)
        assertEquals(366, getDaysBetweenMillis(
            cal1.timeInMillis,
            cal2.timeInMillis
        ))

    }
}