package com.funkytwig.tasktimer

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object TimingsContract {
    internal const val TABLE_NAME = "Timings"

    /**
     * Uri to access table
     */
    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_PROVIDER_URI, TABLE_NAME)
    const val CONTENT_TYPE =
        "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_NAME" // MIME type for rewcords
    const val CONTENT_ITEM_TYPE =
        "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_NAME" // MINI type for record

    object Columns { // Fields
        const val ID = BaseColumns._ID
        const val TIMING_TASK_ID = "TaskId"
        const val TIMING_START_TIME = "StartTime"
        const val TIMING_DURATION = "Duration"
    }

    fun getId(uri: Uri): Long { // Get Id from uri as longs
        return ContentUris.parseId(uri) // Converts lat segment of path(id) to long
    }

    fun buildUriFromId(id: Long): Uri { // Add id to end of Uri
        return ContentUris.withAppendedId(CONTENT_URI, id) // Append id to end of path
    }
}

//SELECT   tim.${TimingsContract.Columns.ID},
//tim.${TimingsContract.Columns.TIMING_TASK_ID},
//tim.${TimingsContract.Columns.TIMING_START_TIME},
//tim.${TasksContract.Columns.TASK_NAME}