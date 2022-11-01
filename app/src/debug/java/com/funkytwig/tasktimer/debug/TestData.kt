package com.funkytwig.tasktimer.debug

import android.content.ContentResolver
import android.content.ContentValues
import com.funkytwig.tasktimer.TasksContract
import com.funkytwig.tasktimer.TimingsContract
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.GregorianCalendar


internal class TestTimings internal constructor(val taskId: Long, date: Long, val duration: Long) {
    var startTime: Long = 0

    init {
        this.startTime = date / 1000
    }
}

object TestData {
    private const val SECS_IN_DAY = 86400
    private const val LOWER_BAND = 100
    private const val UPPER_BAND = 500
    private const val MAX_DURATION = SECS_IN_DAY / 6 // 1/6 day, 4 hours

    fun generateTextDate(contentResolver: ContentResolver) {
        val projection = arrayOf(TasksContract.Columns.ID)
        val uri = TasksContract.CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val taskId = cursor.getLong((cursor.getColumnIndex(TasksContract.Columns.ID)))

                val loopCount = LOWER_BAND + getRandomInt(UPPER_BAND - LOWER_BAND)
                for (i in 0 until loopCount) {
                    val randomDate = randomDateTime()
                    val duration = getRandomInt(MAX_DURATION).toLong()
                    val testTiming = TestTimings(taskId, randomDate, duration)

                    saveCurrentTimings(contentResolver, testTiming)
                }

            } while (cursor.moveToNext())
            cursor.close()
        }
    }

    private fun getRandomInt(max: Int): Int {
        return Math.round(Math.random() * max).toInt()
    }

    private fun randomDateTime(): Long {
        val startYear = 2018
        val endYear = 2019
        val sec = getRandomInt(59)
        val min = getRandomInt(59)
        val hour = getRandomInt(23)
        val month = getRandomInt(11)
        val year = startYear + getRandomInt(endYear - startYear)

        val gc = GregorianCalendar(year, month, 1)
        val day = 1 + getRandomInt(gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - 1)

        gc.set(year, month, day, hour, min, sec)
        return gc.timeInMillis
    }

    private fun saveCurrentTimings(contentResolver: ContentResolver, currentTimings: TestTimings) {
        val values = ContentValues()
        values.put(TimingsContract.Columns.TIMING_TASK_ID, currentTimings.taskId)
        values.put(TimingsContract.Columns.TIMING_START_TIME, currentTimings.startTime)
        values.put(TimingsContract.Columns.TIMING_DURATION, currentTimings.duration)

        // TODO: replace depreciated GlobalScope
        GlobalScope.launch {
            contentResolver.insert(TimingsContract.CONTENT_URI, values)
        }
    }

}