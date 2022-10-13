package com.funkytwig.tasktimer

import android.app.Application
import android.database.Cursor
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "TaskTimerViewModelXX"

class TaskTimerViewModel(application: Application) : AndroidViewModel(application) {
    private val dbCursor = MutableLiveData<Cursor>()
    val cursor: LiveData<Cursor>
        get() = dbCursor

    init {
        Log.d(TAG, "init")
        loadTasks()
    }

    private fun loadTasks() {
        val funct = "loadTasks"
        Log.d(TAG, funct)
        val projection = arrayOf(
            TasksContract.Columns.TASK_NAME,
            TasksContract.Columns.TASK_DESCRIPTION,
            TasksContract.Columns.TASK_SORT_ORDER
        )
        val sortOrder =
            "${TasksContract.Columns.TASK_SORT_ORDER}, ${TasksContract.Columns.TASK_NAME}"
        val cursor = getApplication<Application>().contentResolver.query(
            TasksContract.CONTENT_URI, null, null, projection, sortOrder
        )
        dbCursor.postValue(cursor!!) // Update on different thread
        Log.d(TAG, "$funct done")

    }
}