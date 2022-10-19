package com.funkytwig.tasktimer

import android.app.Application
import android.content.ContentValues
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "TaskTimerViewModelXX"

class TaskTimerViewModel(application: Application) : AndroidViewModel(application) {
    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
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
            TasksContract.CONTENT_URI, true, contentObserver
        )
        loadTasks()
    }

    private fun loadTasks() {
        val func = "loadTasks"
        Log.d(TAG, func)
        val projection = arrayOf(
            TasksContract.Columns.ID,
            TasksContract.Columns.TASK_NAME,
            TasksContract.Columns.TASK_DESCRIPTION,
            TasksContract.Columns.TASK_SORT_ORDER
        )
        val sortOrder =// set sortOrder so cursor is in correct order
            "${TasksContract.Columns.TASK_SORT_ORDER}, ${TasksContract.Columns.TASK_NAME}"
        viewModelScope.launch(Dispatchers.IO) {
            val cursor =
                getApplication<Application>().contentResolver.query(
                    TasksContract.CONTENT_URI, projection, null, null, sortOrder
                )
            dbCursor.postValue(cursor!!) // runs setValue via Handler in MainThread
        }
        Log.d(TAG, "$func done")
    }

    fun saveTask(task: Task): Task {
        val func = "saveTask"
        val values = ContentValues()
        if (task.name.isNotEmpty()) {
            values.put(TasksContract.Columns.TASK_NAME, task.name)
            values.put(TasksContract.Columns.TASK_DESCRIPTION, task.description)
            values.put(TasksContract.Columns.TASK_SORT_ORDER, task.sortOrder)
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (task.id == 0L) { // New Record
                Log.d(TAG, "$func: save data")
                val uri = getApplication<Application>().contentResolver?.insert(
                    TasksContract.CONTENT_URI,
                    values
                )
                if (uri != null) {
                    Log.d(TAG, "$func: saved data uti=$uri")
                    task.id = TasksContract.getId(uri)
                }
            } else { // Update Record
                Log.d(TAG, "$func: saved data name=${task.name}")
                getApplication<Application>().contentResolver?.update(
                    TasksContract.buildUriFromId(task.id), values, null, null
                )
            }
        }
        return task // returning as may have an id
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            getApplication<Application>().contentResolver.delete(
                TasksContract.buildUriFromId(taskId), null, null
            )
        }
    }

    override fun onCleared() {
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }

}