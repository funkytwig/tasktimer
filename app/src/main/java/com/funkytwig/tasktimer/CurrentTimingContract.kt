package com.funkytwig.tasktimer

import android.net.Uri

object CurrentTimingContract {
    internal const val TABLE_NAME = "vwCurrentTimings"

    /**
     * Uri to access vwCurrentTimings view
     */
    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_PROVIDER_URI, TABLE_NAME)
    const val CONTENT_TYPE =
        "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_NAME" // MIME type for rewcords
    const val CONTENT_ITEM_TYPE =
        "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_NAME" // MINI type for record

    object Columns {
        const val TIMINGS_ID = TimingsContract.Columns.ID
        const val TIMING_TASK_ID = TimingsContract.Columns.TIMING_TASK_ID
        const val TIMING_START_TIME = TimingsContract.Columns.TIMING_START_TIME
        const val TASK_NAME = TasksContract.Columns.TASK_NAME
    }
}