package com.funkytwig.tasktimer

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.funkytwig.tasktimer.databinding.FragmentAddEditBinding // NEW

private const val TAG = "XXAddEditFragment"

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
    private lateinit var binding: FragmentAddEditBinding // NEW

    private var task: Task? = null
    private var listener: OnSaveClicked? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        task = arguments?.getParcelable(ARG_TASK)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")

        // binding = FragmentAddEditBinding.inflate(layoutInflater)
        binding = FragmentAddEditBinding.inflate(layoutInflater, container, false)


        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_add_edit, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val func = "onActivityCreated"
        Log.d(TAG, func)
        super.onActivityCreated(savedInstanceState)
        binding.addEditSave.setOnClickListener { listener?.onSaveClicked() }
        Log.d(TAG, "$func: addEditSave listner attached")
    }

    override fun onAttach(context: Context) {
        val func = "onAttach"
        Log.d(TAG, func)
        super.onAttach(context)
        if (context is OnSaveClicked) {
            Log.d(TAG, "$func: Setting listner to context")
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnSaveClicked")
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
}