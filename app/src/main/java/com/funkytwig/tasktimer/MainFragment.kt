package com.funkytwig.tasktimer

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.BuildConfig
import com.funkytwig.tasktimer.databinding.FragmentMainBinding

private const val TAG = "MainFragmentXX"
private const val DIALOG_ID_DELETE = 1 // Dialog ID for delete dialog
private const val DIALOG_TASK_ID = "task_id" // Dialog Bundle Task ID label

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment(), CursorRecyclerViewAdapter.OnTaskClickListner,
    AppDialog.DialogEvents {

    interface OnTaskEdit {
        fun onTaskEdit(task: Task)
    }

    private var _binding: FragmentMainBinding? = null

    //private val viewModel by lazy { ViewModelProvider(this)[TaskTimerViewModel::class.java] }
    private val viewModel: TaskTimerViewModel by activityViewModels() // scope=activity

    private val mAdapter =
        CursorRecyclerViewAdapter(null, this) // null=view with instructions // Change

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        val func = "onCreate"
        Log.d(TAG, func)
        super.onCreate(savedInstanceState)
        viewModel.cursor.observe(this) { cursor -> mAdapter.swapCursor(cursor)?.close() }
        Log.d(TAG, "$func done")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        binding.taskList.layoutManager =
            LinearLayoutManager(context) // Set layout manager to Linear
        binding.taskList.adapter = mAdapter // Attach Adapter to Recyclerview
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
        if (context !is OnTaskEdit) throw RuntimeException("$TAG: $context must implement OnTaskEdit")
    }

    override fun onEditClick(task: Task) {
        (activity as OnTaskEdit?)?.onTaskEdit(task) // think we do it like this as cant get listner passed to Fragment
    }

    override fun onDeleteClick(task: Task) {
        val args = Bundle().apply {
            putInt(DIALOG_ID, DIALOG_ID_DELETE)
            putString(DIALOG_MESSAGE, getString(R.string.deldiag_message, task.id, task.name))
            putInt(DIALOG_POSITIVE_RID, R.string.deldiag_postative_caption) // pass string ID
            putLong(DIALOG_TASK_ID, task.id)
        }
        val dialog = AppDialog()
        dialog.arguments = args
        dialog.show(childFragmentManager, null)
    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle) {
        if (dialogId == DIALOG_ID_DELETE) {
            val taskId = args.getLong(DIALOG_TASK_ID)
            // Assertions used to wan developers
            if (BuildConfig.DEBUG && taskId == 0L) throw AssertionError("Task ID is zero")
            viewModel.deleteTask(taskId)
        } else throw IllegalArgumentException(
            "$TAG: onPositiveDialogResult does not implement dialog ID $dialogId"
        )
    }

    override fun onTaskLongClick(task: Task) {
        TODO("Not yet implemented")
    }
}
