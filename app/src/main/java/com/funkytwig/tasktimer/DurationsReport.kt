package com.funkytwig.tasktimer

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.funkytwig.tasktimer.databinding.ActivityDurationsReportBinding
//import com.funkytwig.tasktimer.databinding.TaskDurationsBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "DurationsReportXX"

enum class SortColumns { NAME, DESCRIPTION, START_DATE, DURATION }

class DurationsReport : AppCompatActivity() {
    private val reportAdapter by lazy { DurationsRVAdapter(this, null) }
    var databaseCursor: Cursor? = null
    var sortOrder = SortColumns.NAME
    private val selection = "${DurationsContract.Columns.START_TIME} BETWEEN ? AND ?"
    private var selectionArgs = arrayOf("0", "1559347199")

    private lateinit var binding: ActivityDurationsReportBinding
//    private lateinit var tdBinding: TaskDurationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val func = "onCreate"
        Log.d(TAG, func)
        super.onCreate(savedInstanceState)

        binding = ActivityDurationsReportBinding.inflate(layoutInflater)
//        tdBinding = TaskDurationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d(TAG, "$func: Setup adapter")

        val tdList: RecyclerView = findViewById(R.id.td_list);
        tdList.layoutManager = LinearLayoutManager(this)
        tdList.setHasFixedSize(true);
        tdList.adapter = reportAdapter

        // This does not work
        // tdBinding.tdList.layoutManager = LinearLayoutManager(this)
        // tdBinding.tdList.setHasFixedSize(true);
        // tdBinding.tdList.adapter = reportAdapter

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
        GlobalScope.launch {
            val cursor = application.contentResolver.query(
                DurationsContract.CONTENT_URI, null, selection, selectionArgs, order
            )
            Log.d(TAG, "$func: cursor.count=${cursor?.count}")
            databaseCursor = cursor
            reportAdapter.swapCursor(cursor)?.close()
        }
    }
}