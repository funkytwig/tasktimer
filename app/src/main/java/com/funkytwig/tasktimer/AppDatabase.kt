package com.funkytwig.tasktimer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Basic Database class for application, only class that should use this is [AppProvider]
 */

private const val TAG = "AppDatabaseXX"
private const val DATABASE_NAME = "TaskTimer.db"
private const val DATABASE_VERSION = 4

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

        addTimingsTable(db)
        addurrentTimingsView(db)
        addDurationsView(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { // removed ?
        Log.d(TAG, "onUpgrade")
        when (oldVersion) {
            1 -> { // upgrade from version 1
                addTimingsTable(db)
                addurrentTimingsView(db)
                addDurationsView(db)

            }
            2 -> { // upgrade from Version 2
                addurrentTimingsView(db)
                addDurationsView(db)
            }
            3 -> { // upgrade from Version 3
                addDurationsView(db)
            }
            else -> throw IllegalAccessException("onUpgrade with unknown new version $newVersion")
        }
    }

    private fun addTimingsTable(db: SQLiteDatabase) {
        val sSQLTimings = """CREATE TABLE ${TimingsContract.TABLE_NAME} (
            ${TimingsContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
            ${TimingsContract.Columns.TIMING_TASK_ID} INTEGER NOT NULL,
            ${TimingsContract.Columns.TIMING_START_TIME} INTEGER, 
            ${TimingsContract.Columns.TIMING_DURATION} INTEGER
        );""".replaceIndent()
        Log.d(TAG, sSQLTimings)
        db.execSQL(sSQLTimings)

        // Trigger to delete Task timings rows if Task deleted
        val sSQLTrigger = """CREATE TRIGGER Remove_Task
            AFTER DELETE ON ${TasksContract.TABLE_NAME}
            FOR EACH ROW BEGIN
              DELETE FROM ${TasksContract.TABLE_NAME}
              WHERE ${TimingsContract.Columns.TIMING_TASK_ID} = OLD.${TasksContract.Columns.ID};
            END;""".replaceIndent()
        Log.d(TAG, sSQLTrigger)
        db.execSQL(sSQLTrigger)
    }

    private fun addurrentTimingsView(db: SQLiteDatabase) {
        val sSQLView = """CREATE VIEW ${CurrentTimingContract.TABLE_NAME} AS
            SELECT   tim.${TimingsContract.Columns.ID}                AS ${CurrentTimingContract.Columns.TIMINGS_ID}, 
                     tim.${TimingsContract.Columns.TIMING_TASK_ID}    AS ${CurrentTimingContract.Columns.TIMING_TASK_ID}, 
                     tim.${TimingsContract.Columns.TIMING_START_TIME} AS ${CurrentTimingContract.Columns.TIMING_START_TIME},
                     tas.${TasksContract.Columns.TASK_NAME}           AS ${CurrentTimingContract.Columns.TASK_NAME}
            FROM     ${TimingsContract.TABLE_NAME} tim 
            JOIN     ${TasksContract.TABLE_NAME}   tas
            ON       tim.${TimingsContract.Columns.TIMING_TASK_ID} =  tas.${TasksContract.Columns.ID}
            WHERE    ${TimingsContract.Columns.TIMING_DURATION} = 0
            ORDER BY ${TimingsContract.Columns.TIMING_START_TIME} DESC;""".replaceIndent()

        Log.d(TAG, sSQLView)
        db.execSQL(sSQLView)
    }

    private fun addDurationsView(db: SQLiteDatabase) {
        val sSQLView = """CREATE VIEW ${DurationsContract.TABLE_NAME} AS
            SELECT       tas.${TasksContract.Columns.TASK_NAME}                                          
                           AS ${DurationsContract.Columns.NAME},
                         tas.${TasksContract.Columns.TASK_DESCRIPTION}                                   
                           AS ${DurationsContract.Columns.DESCRIPTION},
                         tim.${TimingsContract.Columns.TIMING_START_TIME}                                
                           AS ${DurationsContract.Columns.START_TIME},
                         DATE(tim.${TimingsContract.Columns.TIMING_START_TIME},'unixepoch', 'localtime') 
                           AS ${DurationsContract.Columns.START_DATE},
                         SUM(tim.${TimingsContract.Columns.TIMING_DURATION})                             
                           AS ${DurationsContract.Columns.DURATION}
            FROM Tasks   tas
            JOIN Timings tim
            ON   tas.${TasksContract.Columns.ID} = tim.${TimingsContract.Columns.TIMING_TASK_ID}
            GROUP BY tas.${TasksContract.Columns.ID}, ${DurationsContract.Columns.START_DATE};""".replaceIndent()

        Log.d(TAG, sSQLView)
        db.execSQL(sSQLView)
    }

    companion object : SingletonHolder<AppDatabase, Context>(::AppDatabase)
}