package com.funkytwig.tasktimer

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.funkytwig.tasktimer.databinding.ActivityDurationsReportBinding

private const val TAG = "DurationsReportXX"

private const val DIALOG_FILTER = 1 // note default would be 0 NEW
private const val DIALOG_DELETE = 2 // NEW

class DurationsReport : AppCompatActivity(), DatePickerDialog.OnDateSetListener, // CHANGE
    View.OnClickListener {
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

    override fun onClick(v: View) { // setOnClickListener
        Log.d(TAG, "onClick")
        when (v.id) {
            R.id.td_name_heading -> viewModel.sortOrder = SortColumns.NAME
            R.id.td_description_heading -> viewModel.sortOrder = SortColumns.DESCRIPTION
            R.id.td_start_heading -> viewModel.sortOrder = SortColumns.START_DATE
            R.id.td_duration_heading -> viewModel.sortOrder = SortColumns.DURATION
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rm_filter_period -> {
                viewModel.toggleDisplayWeek()
                invalidateOptionsMenu() // redraw menu
                return true
            }
            R.id.rm_filter_date -> {
                showDatePickerDialog(getString(R.string.date_title_filter), DIALOG_FILTER) // NEW
                return true // NEW
            }
            R.id.rm_delete -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.rm_filter_period)
        if (item != null) {
            if (viewModel.displayWeek) {
                item.setIcon(R.drawable.ic_baseline_filter_1_24)
                item.setTitle(R.string.rm_title_filter_day)
            } else {
                item.setIcon(R.drawable.ic_baseline_filter_7_24)
                item.setTitle(R.string.rm_title_filter_week)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun showDatePickerDialog(title: String, dialogId: Int) { // NEW
        val func = "showDatePickerDialog"
        Log.d(TAG, func)
        val dialogFragment = DatePickerFragment()

        val arguments = Bundle()
        arguments.putInt(DATE_PICKER_ID, dialogId)
        arguments.putString(DATE_PICKER_TITLE, title)
        arguments.putLong(DATE_PICKER_DATE, viewModel.getFilterDate().time) // NEW
        dialogFragment.arguments = arguments
        dialogFragment.show(supportFragmentManager, "dataPicker")
        Log.d(TAG, "$func: done")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) { // NEW
        val func = "onDateSet"
        Log.d(TAG, func)

        when (view!!.tag as Int) { // view.tag is DialogId
            DIALOG_FILTER -> {
                viewModel.setReportDate(year, month, dayOfMonth)
            }
            DIALOG_DELETE -> {}
            else -> throw IllegalArgumentException("Invalid mode when receiving DataPickerDialog result")
        }
        Log.d(TAG, "$func: done")
    }
}