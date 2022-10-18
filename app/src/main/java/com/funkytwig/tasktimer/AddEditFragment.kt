package com.funkytwig.tasktimer

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.funkytwig.tasktimer.databinding.FragmentAddEditBinding

private const val TAG = "AddEditFragmentXX"

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TASK = "task"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddEditFragment.OnSaveClicked] interface
 * to handle interaction events.
 * Use the [AddEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class AddEditFragment : Fragment() {
    private lateinit var binding: FragmentAddEditBinding

    //private val viewModel by lazy { ViewModelProvider(this)[TaskTimerViewModel::class.java] }
    private val viewModel: TaskTimerViewModel by activityViewModels() // scope=activity
    private var task: Task? = null
    private var listener: OnSaveClicked? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        // Original code
        task = arguments?.getParcelable(ARG_TASK)

//        // Suggested code but 'Unresolved reference: getParcelableExtra'
//        if (Build.VERSION.SDK_INT >= 33) {
//            task = arguments?.getParcelable(ARG_TASK, User.class)
//        } else {
//            task = arguments?.getParcelable(ARG_TASK)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        binding = FragmentAddEditBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val func = "onViewCreated"
        Log.d(TAG, func)
        super.onViewCreated(view, savedInstanceState)

        // Add listner
        binding.addEditSave.setOnClickListener {
            saveTask()
            listener?.onSaveClicked()
        }

        // Set up Up icon button on actionBar
        if (listener is AppCompatActivity) {
            val actionBar = (listener as AppCompatActivity?)?.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }

        // Populate fields if we are editing Task
        if (savedInstanceState == null) { // this tels us fragment first created and we are not coming back to it
            if (task != null) {
                Log.d(TAG, "$func: found task, editing task ${task?.id}")
                binding.addEditName.setText(task?.name)
                binding.addEditDescription.setText(task?.description)
                binding.addEditSorOrder.setText(task?.sortOrder.toString())
            } else { // Not editing Task, creating new one
                Log.d(TAG, "$func: no task, adding new record ")
            }
        }
    }

    private fun taskFromUi(): Task {
        val sortOrder =
            if (binding.addEditSorOrder.text.isNotEmpty()) Integer.parseInt(binding.addEditSorOrder.text.toString()) else 0
        val newTask = Task(
            binding.addEditName.text.toString(),
            binding.addEditDescription.text.toString(),
            sortOrder
        )
        newTask.id = task?.id ?: 0
        return newTask
    }

    private fun saveTask() {
        Log.d(TAG, "saveTask")
        val newTask = taskFromUi()
        if (newTask != task) { // not the same
            task = viewModel.saveTask(newTask) // get task returned as amy have id
        }


    }

    override fun onAttach(context: Context) {
        val func = "onAttach"
        Log.d(TAG, func)
        super.onAttach(context)
        if (context is OnSaveClicked) {
            Log.d(TAG, "$func: Setting listner to context")
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSaveClicked")
        }
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach")
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other fragments
     * contained in that activity.
     */
    interface OnSaveClicked {
        fun onSaveClicked()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param task The task to be edited, or null to add a new task.
         * @return A new instance of fragment AddEditFragment.
         */
        @JvmStatic
        fun newInstance(task: Task?) = // Convenient way to pass task to fragment
            AddEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK, task)
                }
            }
    }

    // ** From here its just logging functions

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewStateRestored: called")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }
}