package com.funkytwig.tasktimer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Basic Database class for aplicasion, only class that should use this is [AppProvider]
 */

private const val TAG = "AppDatabase"
private const val DATABASE_NAME = "TaskTimer.db"
private const val DATABASE_VERSION = 3

internal class AppDatabase private constructor(contect: Context) :
    SQLiteOpenHelper(contect, DATABASE_NAME, null, DATABASE_VERSION) {

    init { // TODO("Do NOT use this in production code")
        Log.d(TAG, "Initializing database")
    }

    override fun onCreate(db: SQLiteDatabase) { // Called if DB does not exist, removed ?
        Log.d(TAG, "onCreate")
        val sSQL = """CREATE TABLE ${TasksContract.TABLE_NAME} (
            ${TasksContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
            ${TasksContract.Columns.TASK_NAME} TEXT NOT NULL,
            ${TasksContract.Columns.TASK_DESCRIPTION} TEXT,
            ${TasksContract.Columns.TASK_SORT_ORDER} INTEGER
        );""".replaceIndent()
        Log.d(TAG, sSQL)
        db.execSQL(sSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { // removed ?
        Log.d(TAG, "onUpgrade")
        when (oldVersion) {
            1 -> {
                // upgrade from version 1
            }
            else -> throw IllegalAccessException("onUpgrade with unknown new version $newVersion")

        }
    }

    companion object : SingletonHolder<AppDatabase, Context>(::AppDatabase)
}