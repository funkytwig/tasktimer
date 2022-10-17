package com.funkytwig.tasktimer

import android.app.Application
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "TaskTimerViewModelXX"

class TaskTimerViewModel(application: Application) : AndroidViewModel(application) {

    private val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            loadTasks()
        }
    }

    private val dbCursor = MutableLiveData<Cursor>()
    val cursor: LiveData<Cursor>
        get() = dbCursor

    init {
        Log.d(TAG, "init")
        getApplication<Application>().contentResolver.registerContentObserver(
            TasksContract.CONTENT_URI,true, contentObserver
        )
        loadTasks()
    }

    private fun loadTasks() {
        val funct = "loadTasks"
        Log.d(TAG, funct)
        val projection = arrayOf(
            TasksContract.Columns.ID,
            TasksContract.Columns.TASK_NAME,
            TasksContract.Columns.TASK_DESCRIPTION,
            TasksContract.Columns.TASK_SORT_ORDER
        )
        val sortOrder =
            "${TasksContract.Columns.TASK_SORT_ORDER}, ${TasksContract.Columns.TASK_NAME}"
        val cursor = getApplication<Application>().contentResolver.query(
            TasksContract.CONTENT_URI, projection, null, null, sortOrder
        )
        dbCursor.postValue(cursor!!) // Update on different thread
        Log.d(TAG, "$funct done")

    }

    override fun onCleared() {
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }
}