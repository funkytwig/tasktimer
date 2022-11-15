package com.funkytwig.tasktimer

import android.annotation.SuppressLint
import android.app.Application
import android.database.Cursor
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.GregorianCalendar

private const val TAG = "DurationsViewModelXX"

enum class SortColumns { NAME, DESCRIPTION, START_DATE, DURATION }

class DurationsViewModel(application: Application) : AndroidViewModel(application) {
    private val calender = GregorianCalendar()

    private val dbCursor = MutableLiveData<Cursor>()
    val cursor: LiveData<Cursor> get() = dbCursor

    var sortOrder = SortColumns.NAME
        set(order) {
            Log.d(TAG, "sortOrder.set order=$order")
            if (field != order) {
                field = order
                loadData()
            }
        }

    private val selection = "${DurationsContract.Columns.START_TIME} BETWEEN ? AND ?"
    private var selectionArgs = emptyArray<String>()

    private var _displayWeek = true
    val displayWeek: Boolean
        get() = _displayWeek

    init {
        applyFilter()
    }

    fun toggleDisplayWeek() {
        _displayWeek = !_displayWeek
        applyFilter()
    }

    fun getFilterDate(): Date {
        return calender.time
    }

    fun setReportDate(year: Int, month: Int, dayOfMonth: Int) {
        if (calender.get(GregorianCalendar.YEAR) != year
            || calender.get(GregorianCalendar.MONTH) != month
            || calender.get(GregorianCalendar.DAY_OF_MONTH) != dayOfMonth
        ) {
            calender.set(year, month, dayOfMonth, 0, 0, 0)
            applyFilter()
        }
    }

    private fun applyFilter() {
        val func = "applyFilter"
        Log.d(TAG, func)
        val currentCalenderDate = calender.timeInMillis // store time so we can put if back

        if (displayWeek) { // show whole week
            val weekStart = calender.firstDayOfWeek
            Log.d(TAG, "$func: Week, first day=$weekStart")
            Log.d(TAG, "$func: Week, day of week = ${calender.get(GregorianCalendar.DAY_OF_WEEK)}")
            Log.d(TAG, "$func: Week, date = ${calender.time}")
            calender.set(GregorianCalendar.DAY_OF_WEEK, weekStart) // note HOUR is for 12 hour clock
            calender.set(GregorianCalendar.HOUR_OF_DAY, 0)
            calender.set(GregorianCalendar.MINUTE, 0)
            calender.set(GregorianCalendar.SECOND, 0)
            val startDate = calender.timeInMillis / 1000 // set start date seconds in UTC

            calender.add(GregorianCalendar.DATE, 6)
            calender.set(GregorianCalendar.HOUR_OF_DAY, 23) // note HOUR would be for 12 hour clock
            calender.set(GregorianCalendar.MINUTE, 59)
            calender.set(GregorianCalendar.SECOND, 59)
            val endDate = calender.timeInMillis / 1000

            selectionArgs = arrayOf(startDate.toString(), endDate.toString())
            Log.d(TAG, "$func: Week, start=$startDate, end=$endDate")
        } else { // current day, we set date is setReportDate but need to go to beginning of day
            calender.set(GregorianCalendar.HOUR_OF_DAY, 0)
            calender.set(GregorianCalendar.MINUTE, 0)
            calender.set(GregorianCalendar.SECOND, 0)
            val startDate = calender.timeInMillis / 1000

            calender.set(GregorianCalendar.HOUR_OF_DAY, 23)
            calender.set(GregorianCalendar.MINUTE, 59)
            calender.set(GregorianCalendar.SECOND, 59)
            val endDate = calender.timeInMillis / 1000

            selectionArgs = arrayOf(startDate.toString(), endDate.toString())
            Log.d(TAG, "$func: Single Day, start=$startDate, end=$endDate")
        }

        calender.timeInMillis = currentCalenderDate// put calender back to what it was
        loadData()
    }


    @SuppressLint("Recycle")
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
            val cursor =
                getApplication<Application>().contentResolver.query( // observer closes cursor
                    DurationsContract.CONTENT_URI, null, selection, selectionArgs, order
                )
            Log.d(TAG, "$func: cursor.count=${cursor?.count}")
            dbCursor.postValue(cursor!!)
        }
    }
}