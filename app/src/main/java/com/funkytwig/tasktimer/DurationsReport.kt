package com.funkytwig.tasktimer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.funkytwig.tasktimer.databinding.ActivityDurationsReportBinding

private const val TAG = "DurationsReportXX"

class DurationsReport : AppCompatActivity() {
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

        val tdList: RecyclerView = findViewById(R.id.td_list);
        tdList.layoutManager = LinearLayoutManager(this)
        tdList.setHasFixedSize(true);
        tdList.adapter = reportAdapter

        viewModel.cursor.observe(this, Observer { cursor -> reportAdapter.swapCursor(cursor)?.close() })
    }
}