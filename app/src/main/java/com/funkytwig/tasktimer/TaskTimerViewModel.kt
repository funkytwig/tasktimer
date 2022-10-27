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

    private var currentTiming: Timing? = null

    private val dbCursor = MutableLiveData<Cursor>()
    val cursor: LiveData<Cursor> get() = dbCursor

    // LiveData item for Current Task name on MainFragment
    private val taskTimings = MutableLiveData<String>()
    val timing: LiveData<String> get() = taskTimings

    init {
        Log.d(TAG, "init")
        getApplication<Application>().contentResolver.registerContentObserver(
            TasksContract.CONTENT_URI, true, contentObserver
        )
        currentTiming = retrieveTiming() // put first so user cant start another task
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

    fun retrieveTiming(): Timing? {
        val func = "retrieveTiming"
        Log.d(TAG, func)
        // Get currentTiming record if app closed and timing was happening
        val timing: Timing?

        val timimgCursor: Cursor? = getApplication<Application>().contentResolver.query(
            CurrentTimingContract.CONTENT_URI, null, // all columns
            null, null, null
        )

        // access DB on main thread so tasktimings LiveData gets updated before tasks shown so they
        // cant long tap a task first
        if (timimgCursor != null && timimgCursor.moveToFirst()) {
            val id = timimgCursor.getLong(timimgCursor.getColumnIndex(CurrentTimingContract.Columns.TIMINGS_ID))
            val taskId = timimgCursor.getLong(timimgCursor.getColumnIndex(CurrentTimingContract.Columns.TIMING_TASK_ID))
            val startTime = timimgCursor.getLong(timimgCursor.getColumnIndex(CurrentTimingContract.Columns.TIMING_START_TIME))
            val name = timimgCursor.getString(timimgCursor.getColumnIndex(CurrentTimingContract.Columns.TASK_NAME))
            timing = Timing(taskId, startTime, id)
            taskTimings.value = name // Update LiveData object
        } else {
            timing = null
        }

        timimgCursor?.close()
        return timing
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

    fun timeTask(task: Task) { // Called when user long tappes a task
        val func = "timeTask"
        Log.d(TAG, "$func ${currentTiming.toString()}")
        val timingRecord = currentTiming // Smart Task Hack TODO: try without this, hacks not good

        if (timingRecord == null) { // no task being timed, start new timing on current task
            Log.d(TAG, "$func: New task timer started")
            currentTiming = Timing(task.id) // TODO: repeated code, make function
            saveTiming(currentTiming!!)
        } else { // task being timed, update duration and save it (i.e. stop it)
            Log.d(TAG, "$func: Timer running, stop it")
            timingRecord.setDuration()
            saveTiming(currentTiming!!)

            if (task.id == timingRecord!!.taskId) { // Current record tapped a second time, stop timing
                Log.d(TAG, "$func: We long tapped on current timer")
                currentTiming = null // Timing  saves, reset ready for new Timing
            } else { // User long taped a different task, we have stopped current task already
                Log.d(TAG, "$func: We long tapped on a different timer")
                currentTiming = Timing(task.id) // TODO: repeated code, make function
                saveTiming(currentTiming!!) // This will insert new timing with 0 duration
                // alternative solution without !!
                // val newTiming = Timing(task.id)
                // saveTiming(newTiming)
                // currentTiming = newTiming
                // nut !! probably better as you may roget to do last line
            }
        }


        // update the LiveData used for current task message
        taskTimings.value = // TODO: Should make Currently Timing a string resource
            if (currentTiming != null)
                getApplication<Application>().resources.getString(
                    R.string.timing_message, task.name
                )
            else
                getApplication<Application>().resources.getString(R.string.no_task_message)
    }

    private fun saveTiming(currentTiming: Timing) {
        val func = "saveTiming"
        Log.d(TAG, "$func $currentTiming")

        val inserting = (currentTiming.duration == 0L) // If duration is 0 insert else update row

        val values = ContentValues().apply {
            if (inserting) { // These are not needed for update
                put(TimingsContract.Columns.TIMING_TASK_ID, currentTiming.taskId)
                put(TimingsContract.Columns.TIMING_START_TIME, currentTiming.startTime)
            }
            put(TimingsContract.Columns.TIMING_DURATION, currentTiming.duration)
        }

        Log.d(TAG, "$func: values=$values")

        viewModelScope.launch(Dispatchers.IO) {
            if (inserting) {
                val uri = getApplication<Application>().contentResolver.insert(
                    TimingsContract.CONTENT_URI, values
                )
                if (uri != null) {
                    currentTiming.id = TimingsContract.getId(uri) // put ID back into timing object
                } // ?else I think we should throw an error?
            } else {
                getApplication<Application>().contentResolver.update(
                    TimingsContract.buildUriFromId(currentTiming.id),
                    values, null, null
                )
            }
        }
    }


    override fun onCleared() {
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }

}