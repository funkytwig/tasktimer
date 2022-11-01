package com.funkytwig.tasktimer

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext

/**
 * Provider for the TaskTimer app.  This is the only class that knows about [AppDatabase]
 */

private const val TAG = "AppProviderXX"

const val CONTENT_AUTHORITY = "com.funkytwig.tasktimer.provider" // unique provider name

// The following constants are the integers returned by the UriMatched depending on the Uri.
private const val TASKS = 100
private const val TASKS_ID = 101

private const val TIMINGS = 200
private const val TIMINGS_ID = 201

private const val CURRENT_TIMINGS = 300

private const val TASK_DURATIONS = 400

val CONTENT_PROVIDER_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY") // usable outside app

class AppProvider : ContentProvider() {
    private val uriMatcher by lazy { buildUriMatcher() }

    private fun buildUriMatcher(): UriMatcher { // Helps us parse out the table name
        Log.d(TAG, "buildUriMatcher")
        val matcher = UriMatcher(UriMatcher.NO_MATCH) // NO_MATCH if root URi matched

        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS)
        matcher.addURI(CONTENT_AUTHORITY, "${TasksContract.TABLE_NAME}/#", TASKS_ID) // #=number
        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS)
        matcher.addURI(CONTENT_AUTHORITY, "${TimingsContract.TABLE_NAME}/#", TIMINGS_ID)
        matcher.addURI(CONTENT_AUTHORITY, CurrentTimingContract.TABLE_NAME, CURRENT_TIMINGS)
        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASK_DURATIONS)

        return matcher
    }

    override fun onCreate(): Boolean {
        val func = "onCreate"
        Log.d(TAG, func)
        return true // We are creating DB in AppDatabase singleton
    }

    override fun getType(uri: Uri): String { // Get MIME type for Uri
        val match = uriMatcher.match(uri)
        Log.d(TAG, "getType match $match")
        return when (match) {
            TASKS -> TasksContract.CONTENT_TYPE
            TASKS_ID -> TasksContract.CONTENT_ITEM_TYPE
            TIMINGS -> TimingsContract.CONTENT_TYPE
            TIMINGS_ID -> TimingsContract.CONTENT_ITEM_TYPE
            CURRENT_TIMINGS -> CurrentTimingContract.CONTENT_ITEM_TYPE
            TASK_DURATIONS -> TimingsContract.CONTENT_TYPE
            else -> throw IllegalAccessException("Unknown Uri: $uri")
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?, // String array of columns to return
        selection: String?, // WHERE columns
        selectionArgs: Array<out String>?, // Where values for columns
        sortOrder: String? // ORDER BY
    ): Cursor? {
        val func = "query"
        Log.d(TAG, "$func for uri=$uri")
        val match = uriMatcher.match(uri)

        val queryBuilder = SQLiteQueryBuilder()

        when (match) {
            TASKS -> queryBuilder.tables = TasksContract.TABLE_NAME

            TASKS_ID -> {
                queryBuilder.tables = TasksContract.TABLE_NAME
                val taskId = TasksContract.getId(uri)
                Log.d(TAG, "$func: $taskId")
                queryBuilder.appendWhere("${TasksContract.Columns.ID} =")
                queryBuilder.appendWhereEscapeString("$taskId")
            }

            TIMINGS -> queryBuilder.tables = TimingsContract.TABLE_NAME

            TIMINGS_ID -> {
                queryBuilder.tables = TimingsContract.TABLE_NAME
                val timingsId = TimingsContract.getId(uri)
                queryBuilder.appendWhere("${TimingsContract.Columns.ID} =")
                queryBuilder.appendWhereEscapeString("$timingsId")
            }

            CURRENT_TIMINGS -> queryBuilder.tables = CurrentTimingContract.TABLE_NAME

            TASK_DURATIONS -> queryBuilder.tables = DurationsContract.TABLE_NAME

            else -> throw IllegalAccessException("Unknown URI: $uri")
        }

        val context = requireContext(this) // Get NotNull context for ContentProvider
        val db = AppDatabase.getInstance(context).readableDatabase
        val cursor =
            queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        Log.d(TAG, "$func: ${cursor.count} rows returned") // TODO remove this line
        return cursor
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // accepts a uri and return uri with ID added
        val func = "insert"
        Log.d(TAG, func)
        Log.d(TAG, "$func $values")

        if (values == null) throw IllegalAccessException("$func ContentValues can not be null")

        val recordId: Long
        val returnUri: Uri

        val context = requireContext(this) // Get NotNull context for ContentProvider
        // was told not to do this as its is 'slow' but that's only if thee is a problem (invalid Uri)
        val db = AppDatabase.getInstance(context).writableDatabase

        // writableDatabase and readableDatabase slow so do not call if invalid Uri
        val match = uriMatcher.match(uri)
        Log.d(TAG, "$func: match=$match for uri=$uri")
        when (match) {
            TASKS -> {
                recordId = db.insert(TasksContract.TABLE_NAME, null, values)
                if (recordId != -1L)
                    returnUri = TasksContract.buildUriFromId(recordId)
                else throw IllegalAccessException("Failed to insert $uri")
            }
            TIMINGS -> {
                recordId = db.insert(TimingsContract.TABLE_NAME, null, values)
                if (recordId != -1L)
                    returnUri = TimingsContract.buildUriFromId(recordId)
                else throw IllegalAccessException("Failed to insert $uri")
            }
            else -> throw IllegalAccessException("Unknown Uri: $uri")
        }
        Log.d(TAG, "$func Inserted Uri $returnUri")
        if (recordId > 0) context.contentResolver?.notifyChange(uri, null)
        return returnUri
    }


    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val func = "update"
        Log.d(TAG, "$func $uri")
        Log.d(TAG, "$func: $values")

        if (values == null) throw IllegalAccessException("$func ContentValues can not be null")

        val count: Int
        var selectionCriteria: String

        val context = requireContext(this) // Get NotNull context for ContentProvider
        // was told not to do this as its is 'slow' but that's only if thee is a problem (invalid Uri)
        val db = AppDatabase.getInstance(context).writableDatabase

        val match = uriMatcher.match(uri)
        Log.d(TAG, "$func: match=$match for uri=$uri")
        when (match) {
            TASKS -> // Whole table + possible selection
                count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs)
            TASKS_ID -> { // single ID + possible selection
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"
                if (selection != null && selection.isNotEmpty()) selectionCriteria += " AND ($selection)"
                count =
                    db.update(TasksContract.TABLE_NAME, values, selectionCriteria, selectionArgs)
            }
            TIMINGS ->  // Whole table + possible selection
                count = db.update(TimingsContract.TABLE_NAME, values, selection, selectionArgs)
            TIMINGS_ID -> { // single ID + possible selection
                val id = TimingsContract.getId(uri)
                selectionCriteria = "${TimingsContract.Columns.ID} = $id"
                if (selection != null && selection.isNotEmpty()) selectionCriteria += " AND ($selection)"
                count =
                    db.update(TimingsContract.TABLE_NAME, values, selectionCriteria, selectionArgs)
            }
            else -> throw IllegalAccessException("Unknown Uri: $uri")
        }

        Log.d(TAG, "$func: Updated Uri $uri Count $count")
        if (count > 0) context.contentResolver?.notifyChange(uri, null) // NEW
        return count
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val func = "delete"
        Log.d(TAG, func)

        val count: Int
        var selectionCriteria: String

        val context = requireContext(this) // Get NotNull context for ContentProvider
        // was told not to do this as its is 'slow' but that's only if thee is a problem (invalid Uri)
        val db = AppDatabase.getInstance(context).writableDatabase

        val match = uriMatcher.match(uri)
        Log.d(TAG, "$func: match=$match for uri=$uri")
        when (match) {
            TASKS ->  // Whole table + possible selection
                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs)
            TASKS_ID -> { // single ID + possible selection
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"
                if (selection != null && selection.isNotEmpty()) selectionCriteria += " AND ($selection)"
                count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs)
            }
            TIMINGS ->  // Whole table + possible selection
                count = db.delete(TimingsContract.TABLE_NAME, selection, selectionArgs)
            TIMINGS_ID -> { // single ID + possible selection
                val id = TimingsContract.getId(uri)
                selectionCriteria = "${TimingsContract.Columns.ID} = $id"
                if (selection != null && selection.isNotEmpty()) selectionCriteria += " AND ($selection)"
                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs)
            }
            else -> throw IllegalAccessException("Unknown Uri: $uri")
        }

        Log.d(TAG, "$func: Deleted Uri $uri Count $count")
        if (count > 0) context.contentResolver?.notifyChange(uri, null)
        return count
    }
}