package com.funkytwig.tasktimer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.funkytwig.tasktimer.databinding.ActivityDurationsReportBinding

private const val TAG = "DurationsReportXX"

class DurationsReport : AppCompatActivity(), View.OnClickListener {
    private val viewModel: DurationsViewModel by viewModels() // scope=activity

    private val reportAdapter by lazy { DurationsRVAdapter(this, null) }

    private lateinit var binding: ActivityDurationsReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val func = "onCreate"
        Log.d(TAG, func)
        super.onCreate(savedInstanceState)
        binding = ActivityDurationsReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup adapter
        binding.items.tdList.layoutManager = LinearLayoutManager(this)
        binding.items.tdList.setHasFixedSize(true)
        binding.items.tdList.adapter = reportAdapter
        viewModel.cursor.observe(this) { cursor -> reportAdapter.swapCursor(cursor)?.close() }

        binding.items.tdNameHeading.setOnClickListener(this)
        binding.items.tdStartHeading.setOnClickListener(this) // But this does not
        binding.items.tdDurationHeading.setOnClickListener(this)
        binding.items.tdDescriptionHeading?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        Log.d(TAG,"onClick")
        when (v.id) {
            R.id.td_name_heading -> viewModel.sortOrder = SortColumns.NAME
            R.id.td_description_heading -> viewModel.sortOrder = SortColumns.DESCRIPTION
            R.id.td_start_heading -> viewModel.sortOrder = SortColumns.START_DATE
            R.id.td_duration_heading -> viewModel.sortOrder = SortColumns.DURATION
        }
    }
}