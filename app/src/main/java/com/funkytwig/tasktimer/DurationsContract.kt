package com.funkytwig.tasktimer

import android.net.Uri

object DurationsContract {
    internal const val TABLE_NAME = "vwTaskDurations"

    /**
     * Uri to access table
     */
    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_PROVIDER_URI, TABLE_NAME)
    const val CONTENT_TYPE =
        "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_NAME" // MIME type for rewcords
    const val CONTENT_ITEM_TYPE =
        "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_NAME" // MINI type for record

    object Columns {
        const val NAME = TasksContract.Columns.TASK_NAME
        const val DESCRIPTION = TasksContract.Columns.TASK_DESCRIPTION
        const val START_TIME = TimingsContract.Columns.TIMING_START_TIME
        const val START_DATE = "StartDate"
        const val DURATION = TimingsContract.Columns.TIMING_DURATION

    }
}
