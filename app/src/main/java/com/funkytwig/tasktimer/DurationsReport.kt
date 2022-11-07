package com.funkytwig.tasktimer

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.funkytwig.tasktimer.databinding.ActivityDurationsReportBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "DurationsReportXX"

enum class SortColumns { NAME, DESCRIPTION, START_DATE, DURATION }

class DurationsReport : AppCompatActivity() {
    private val reportAdapter by lazy { DurationsRVAdapter(this, null) }
    var databaseCursor: Cursor? = null
    var sortOrder = SortColumns.NAME
    private val selection = "${DurationsContract.Columns.START_TIME} BETWEEN ? AND ?"
    private var selectionArgs = arrayOf("0", "155934719999")

    private lateinit var binding: ActivityDurationsReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val func = "onCreate"
        Log.d(TAG, func)
        super.onCreate(savedInstanceState)

        binding = ActivityDurationsReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val tdList: RecyclerView = findViewById(R.id.td_list);
        tdList.layoutManager = LinearLayoutManager(this)
        tdList.setHasFixedSize(true);
        tdList.adapter = reportAdapter

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
//                DurationsContract.CONTENT_URI, null, selection, selectionArgs, order
                DurationsContract.CONTENT_URI, null, null, null, order
            )
            Log.d(TAG, "$func: cursor.count=${cursor?.count}")
            databaseCursor = cursor
            reportAdapter.swapCursor(cursor)?.close()
        }
    }
}