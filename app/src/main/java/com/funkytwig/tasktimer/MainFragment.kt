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
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val viewModel by lazy { ViewModelProvider(this)[TaskTimerViewModel::class.java] } // New
    private val mAdapter = CursorRecyclerViewAdapter(null) // null=view with instructions NEW

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        val funct = "onCreate"
        Log.d(TAG, funct)
        super.onCreate(savedInstanceState)
        Log.d(TAG, "$funct about to register viewModel")
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
            LinearLayoutManager(context) // Set layout manager to Linear NEW
        binding.taskList.adapter = mAdapter // Attach Adapter to Recyclerview New
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }

    // ** From here ist just logging functions **

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) { // depreciated
//        Log.d(TAG, "onActivityCreated(depreciated)")
//        super.onActivityCreated(savedInstanceState)
//    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewStateRestored")
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

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach")
        super.onDetach()
    }
}