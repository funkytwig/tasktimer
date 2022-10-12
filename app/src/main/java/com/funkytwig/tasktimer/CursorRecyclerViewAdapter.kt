package com.funkytwig.tasktimer

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.funkytwig.tasktimer.databinding.FragmentAddEditBinding


private const val TAG = "CurRecViewAdapterXX"

//class TaskViewHolder(override val containerView: View) :
//    RecyclerView.ViewHolder(containerView),
//    LayoutContainer {
//}


class TaskViewHolder(private val containerView: View) :
    RecyclerView.ViewHolder(containerView) {
    var taskListName: TextView = containerView.findViewById(R.id.taskListName)
    var taskListDescription: TextView = containerView.findViewById(R.id.taskListDescription)
    var taskListEdit: ImageButton = containerView.findViewById(R.id.taskListEdit)
    var taskListDelete: ImageButton = containerView.findViewById(R.id.taskListDelete)
}

class CursorRecyclerViewAdapter(private var cursor: Cursor?) :
    RecyclerView.Adapter<TaskViewHolder>() {

    // Called by Recyclerview when it needs new view to display
    // viewType allows different types to be shows on different lines of view,
    // to find out more google 'Recyclerview getItemViewType
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(TAG, "onCreateViewHolder (new view requested)")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_list_items, parent, false)
        return TaskViewHolder(view)
    }

    // When Recycler view wants new data to be displayed and is providing existing view to be reused
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val func = "onBindViewHolder"
        Log.d(TAG, "$func (bind data to view)")

        val cursor = cursor // Smart Cast Hack

        if (cursor == null || cursor.count == 0) { // No items in cursor

            Log.d(TAG, "$func: providing instructions ")
            holder.taskListName.setText(R.string.instructions_heading)
            holder.taskListDescription.setText(R.string.instructions)
            holder.taskListEdit.visibility = View.GONE
            holder.taskListDelete.visibility = View.GONE

        } else { // Cursor not empty

            if (!cursor.moveToPosition(position))
                throw IllegalStateException("Could not move cursor to position $position")
            // Create Task object from data in cursor

            val task = Task(
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_NAME)),
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASK_SORT_ORDER))
            )
            // Remember ID is not set in constructive
            task.id = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns.ID))

            holder.taskListName.text = task.name
            holder.taskListDescription.text = task.description
            holder.taskListEdit.visibility = View.VISIBLE // TODO: add onclick
            holder.taskListDelete.visibility = View.VISIBLE // TODO: add onclick
        }
    }

    override fun getItemCount(): Int {
        val func = "getItemCount"
        Log.d(TAG, func)
        val count = cursor?.count
        if (count == 0 || cursor == null) {
            Log.d(TAG, "$func: no items so return 1")
            return 1
        } else {
            Log.d(TAG, "$func: $count items")
            return count!!.toInt()
        }
    }

    /**
     * Swap is a new cursor, returning the old cursor.
     * The returned cursor is *not* closed
     *
     * This allows underlying cursor to be swapped if data changes and we need to re query
     * Should be called when the cursor that the adapter is using is changed.
     * Reruns previous cursor so it can be closed.
     *
     * @param newCursor The new cursor to be used if there was not one.
     * If the given new cursor is the same as the previous set cursor, null is also returned.
     */
    fun swapcursor(newCursor: Cursor?): Cursor? {
        if (newCursor === cursor) return null

        val numItems = itemCount
        val oldCursor = cursor

        if (newCursor != null) {
            // notify observer about cursor
            notifyDataSetChanged()
        } else { // cursor has changed
            // Notify observer about lack of dataset, all of it from 0 to newItems,
            // i.e. whole range of records has gone
            notifyItemRangeChanged(0, numItems)
        }

        return oldCursor
    }

}