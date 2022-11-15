package com.funkytwig.tasktimer

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatDialogFragment
import java.util.Date
import java.util.GregorianCalendar

private const val TAG = "DatePickerFragmentXX"

const val DATE_PICKER_ID = "ID"
const val DATE_PICKER_TITLE = "TITLE"
const val DATE_PICKER_DATE = "DATE"

class DatePickerFragment : AppCompatDialogFragment(), DatePickerDialog.OnDateSetListener {
    private var dialogId = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cal = GregorianCalendar()
        var title: String? = null
        val arguments = arguments // Smart Cast Hack

        if (arguments != null) {
            dialogId = arguments.getInt(DATE_PICKER_ID)
            title = arguments.getString(DATE_PICKER_TITLE)

            // if date passed use it otherwise use current date
            val givenDate = Date(arguments.getLong(DATE_PICKER_DATE))
            if (givenDate != null) {
                cal.time = givenDate
                Log.d(TAG, "givenDate=$givenDate")
            }
        }

        val year = cal.get(GregorianCalendar.YEAR)
        val month = cal.get(GregorianCalendar.MONTH)
        val day = cal.get(GregorianCalendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), this, year, month, day)

        if (title != null) dpd.setTitle(title)
        return dpd
    }

    override fun onAttach(context: Context) {
        if (context !is DatePickerDialog.OnDateSetListener) // Check callback exists
            throw ClassCastException("$context must implement DatePickerDialog.OnDateSetListener interface")
        super.onAttach(context)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val func = "onDateSet"
        Log.d(TAG, func)
        // notify caller of the user -selected values
        view.tag = dialogId // pass the ID back in the tag, to save the caller storing a copy
        (context as DatePickerDialog.OnDateSetListener?)?.onDateSet(view, year, month, dayOfMonth)
        Log.d(TAG, "$func: done")
    }
}