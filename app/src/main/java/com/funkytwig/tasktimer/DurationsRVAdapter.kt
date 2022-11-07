package com.funkytwig.tasktimer

import android.content.Context
import android.database.Cursor
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.funkytwig.tasktimer.databinding.TaskDurationItemsBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "DurationsRVAdapterXX"

class DurationsRVAdapter(context: Context, private var cursor: Cursor?) :
    RecyclerView.Adapter<DurationsRVAdapter.DurationsViewHolder>() {

    inner class DurationsViewHolder(val bindings: TaskDurationItemsBinding) :
        RecyclerView.ViewHolder(bindings.root)

    private val dateFormat = DateFormat.getDateFormat(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DurationsViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        val view =
            TaskDurationItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DurationsViewHolder(view)

    }

    override fun onBindViewHolder(holder: DurationsViewHolder, position: Int) {
        val func = "onBindViewHolder"
        Log.d(TAG, "$func: position = $position")
        val cursor = cursor

        if (cursor != null && cursor.count != 0) {
            if (!cursor.moveToPosition(position)) {
                throw IllegalStateException("Couldn't move cursor to position $position")
            }
            val name = cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.NAME))
            val description =
                cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.DESCRIPTION))
            val startTime =
                cursor.getLong(cursor.getColumnIndex(DurationsContract.Columns.START_TIME))
            val totalDuration =
                cursor.getLong(cursor.getColumnIndex(DurationsContract.Columns.DURATION))
            val userDate =
                dateFormat.format(startTime * 1000) // The database stores seconds, we need milliseconds
            val totalTime = formatDuration(totalDuration)

            holder.bindings.tdName.text = name
            holder.bindings.tdDescription?.text = description
            holder.bindings.tdStart.text = userDate
            holder.bindings.tdDuration.text = totalTime
        }
    }

    private fun formatDuration(duration: Long): String {
        // convert duration Long to hours:mins:secs String (can be > 24 hours so cant use dateFormat)
        val hours = duration / 3600
        val remainder = duration - hours * 3600
        val minutes = remainder / 60
        val seconds = remainder % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun getItemCount(): Int {
        val func = "getItemCount"
        val count = cursor?.count ?: 0
        Log.d(TAG, "$func: count=$count")
        return count
    }

    fun swapCursor(newCursor: Cursor?): Cursor? {
        val func = "swapCursor"
        Log.d(TAG, func)
        if (newCursor === cursor) return null

        val numItems = itemCount
        val oldCursor = cursor

        cursor = newCursor
        Log.d(TAG, "$func: cursor.count=${cursor?.count}")

        Log.d(TAG, "$func newCursor.count=${newCursor?.count}, oldCursor.cont=${oldCursor?.count}")

        if (newCursor != null) {
            Log.d(TAG, "$func notify the observers about the new cursor")
            // notify the observers about the new cursor
            this.notifyDataSetChanged()
            Log.d(TAG, "$func: notifyDataSetChanged")
        } else {
            Log.d(TAG, "$func Notify observer about lack of dataset")
            // Notify observer about lack of dataset, all of it from 0 to newItems,
            // i.e. whole range of records has gone
            this.notifyItemRangeChanged(0, numItems)
            Log.d(TAG, "$func: notifyItemRangeChanged(0, $numItems)")
        }
        return oldCursor
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val func = "onAttachedToRecyclerView"
        Log.d(TAG, "$func: ${recyclerView.adapter.toString()}")
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        val func = "onDetachedFromRecyclerView"
        Log.d(TAG, "$func: ${recyclerView.adapter.toString()}")
        super.onDetachedFromRecyclerView(recyclerView)
    }
}