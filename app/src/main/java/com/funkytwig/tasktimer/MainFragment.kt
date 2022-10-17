package com.funkytwig.tasktimer

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.funkytwig.tasktimer.databinding.FragmentMainBinding


private const val TAG = "MainFragmentXX"

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment(), CursorRecyclerViewAdapter.OnTaskClickListner { // change
    private var _binding: FragmentMainBinding? = null
    private val viewModel by lazy { ViewModelProvider(this)[TaskTimerViewModel::class.java] }
    private val mAdapter = CursorRecyclerViewAdapter(null, this) // null=view with instructions // Change

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        val funct = "onCreate"
        Log.d(TAG, funct)
        super.onCreate(savedInstanceState)
        viewModel.cursor.observe( // New
            this, Observer { cursor -> mAdapter.swapCursor(cursor)?.close() }
        )
        Log.d(TAG, "$funct done")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
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
    }

    override fun onEditClick(task: Task) {
        TODO("Not yet implemented")
    }

    override fun onDelereClick(task: Task) {
        TODO("Not yet implemented")
    }

    override fun onTaskLongClick(task: Task) {
        TODO("Not yet implemented")
    }
}
