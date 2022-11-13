package com.funkytwig.tasktimer

import android.app.Application
import android.database.Cursor
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "DurationsViewModelXX"

enum class SortColumns { NAME, DESCRIPTION, START_DATE, DURATION }

class DurationsViewModel(application: Application) : AndroidViewModel(application) {
    private val dbCursor = MutableLiveData<Cursor>()
    val cursor: LiveData<Cursor> get() = dbCursor

    var sortOrder = SortColumns.NAME
        set(order) {
            if (field != order) {
                field = order
                loadData()
            }
        }

    private val selection = "${DurationsContract.Columns.START_TIME} BETWEEN ? AND ?"
    private var selectionArgs = arrayOf("1517592695", "1518900878")

    init {
        loadData()
    }

    private fun loadData() {
        val func = "loadData"
        Log.d(TAG, func)
        val order = when (sortOrder) {
            SortColumns.NAME -> DurationsContract.Columns.NAME
            SortColumns.DESCRIPTION -> DurationsContract.Columns.DESCRIPTION
            SortColumns.START_DATE -> DurationsContract.Columns.START_DATE
            SortColumns.DURATION -> DurationsContract.Columns.DURATION
        }
        Log.d(TAG, "order=$order")
        viewModelScope.launch(Dispatchers.IO) {
            val cursor = getApplication<Application>().contentResolver.query(
                DurationsContract.CONTENT_URI, null, selection, selectionArgs, order
            )
            Log.d(TAG, "$func: cursor.count=${cursor?.count}")
            dbCursor.postValue(cursor!!)
        }
    }
}