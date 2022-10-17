package com.funkytwig.tasktimer

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.funkytwig.tasktimer.databinding.TaskListItemsBinding

private const val TAG = "CurRecViewAdapterXX"

class TaskViewHolder(private val binding: TaskListItemsBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val taskListName: TextView = binding.taskListName
    val taskListDescription: TextView = binding.taskListDescription
    val taskListEdit: ImageButton = binding.taskListEdit
    val taskListDelete: ImageButton = binding.taskListDelete

    fun bind(task: Task) { // could do this in onBindViewHolder
        taskListName.text = task.name
        taskListDescription.text = task.description
        taskListEdit.visibility = View.VISIBLE
        taskListDelete.visibility = View.VISIBLE

        taskListEdit.setOnClickListener {
            Log.d(TAG, "Edit clicked ${task.name}")
        }

        taskListDelete.setOnClickListener {
            Log.d(TAG, "Delete clicked ${task.name}")
        }

        binding. containerView.setOnLongClickListener {
            Log.d(TAG, "long clicked view ${task.name}")
            true
        }
    }
}

class CursorRecyclerViewAdapter(private var cursor: Cursor?) :
    RecyclerView.Adapter<TaskViewHolder>() {

    // Called by Recyclerview when it needs new view to display
    // viewType allows different types to be shows on different lines of view,
    // to find out more google 'Recyclerview getItemViewType
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        val viewHolder =
            TaskListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(viewHolder)
    }

    // When Recycler view wants new data to be displayed and is providing existing view to be reused
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val func = "onBindViewHolder"
        val cursor = cursor // Smart Cast Hack

        if (cursor == null || cursor.count == 0) { // No items in cursor
            Log.d(TAG, "$func: cursor empty")
            holder.taskListName.setText(R.string.instructions_heading)
            holder.taskListDescription.setText(R.string.instructions)
            holder.taskListEdit.visibility = View.GONE
            holder.taskListDelete.visibility = View.GONE
        } else { // Cursor not empty
            Log.d(TAG, "$func: cursor NOT empty")
            if (!cursor.moveToPosition(position)) throw IllegalStateException("Could not move cursor to position $position")
            // Create Task object from data in cursor
            val task = Task(
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_NAME)),
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASK_SORT_ORDER))
            )
            // Remember ID is not set in constructor
            task.id = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns.ID))
//            holder.taskListName.text = task.name
//            holder.taskListDescription.text = task.description
//            holder.taskListEdit.visibility = View.VISIBLE // TODO: add onclick
//            holder.taskListDelete.visibility = View.VISIBLE // TODO: add onclick
            holder.bind(task)
        }
    }

    override fun getItemCount(): Int {
        val func = "getItemCount"
        val count = cursor?.count
        if (count == 0 || cursor == null) {
            Log.d(TAG, "$func: no items so return 1")
            return 1 // So Instructions are displayed if cursor empty
        } else {
            Log.d(TAG, "$func: $count items")
            return count!!.toInt()
        }
    }

    /**
     * Swap is a new cursor, returning the old cursor & tell observer it has changed.
     * The returned cursor is *not* closed
     *
     * This allows underlying cursor to be swapped if data changes and we need to re query
     * Should be called when the cursor that the adapter is using is changed.
     * Reruns previous cursor so it can be closed.
     *
     * @param newCursor The new cursor to be used if there was not one.
     * If the given new cursor is the same as the previous set cursor, null is also returned.
     */
    fun swapCursor(newCursor: Cursor?): Cursor? {
        val func = "swapCursor"
        if (newCursor === cursor) return null

        val numItems = itemCount
        val oldCursor = cursor
        cursor = newCursor

        if (newCursor != null) {
            Log.d(TAG, "$func new & previous cursor unchanged")
            // notify the observers about the new cursor
            notifyDataSetChanged()
        } else {
            Log.d(TAG, "$func new & previous cursor different")
            // Notify observer about lack of dataset, all of it from 0 to newItems,
            // i.e. whole range of records has gone
            notifyItemRangeChanged(0, numItems)
        }
        return oldCursor
    }
}