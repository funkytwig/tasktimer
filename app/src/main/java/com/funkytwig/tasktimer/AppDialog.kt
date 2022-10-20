package com.funkytwig.tasktimer

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment


private const val TAG = "AppDialogXX"

const val DIALOG_ID = "id"
const val DIALOG_MESSAGE = "message"
const val DIALOG_POSITIVE_RID = "positive_rid"
const val DIALOG_NEGATIVE_RID = "negative_rid"

class AppDialog : AppCompatDialogFragment() { // note fragments cant have parameters
    private var dialogEvents: DialogEvents? = null

    interface DialogEvents {
        fun onPositiveDialogResult(dialogId: Int, args: Bundle)
        // fun onNegativeDialogResult(dialogId: Int, args: Bundle)
        // fun onDialogCancelled(dialogId: Int)
    }

    override fun onAttach(context: Context) {
        val func = "onAttach"
        Log.d(TAG, func)
        super.onAttach(context)
        // Activities/Fragments containing this fragment must implement its callbacks.
        Log.d(TAG, "$func: parentFragment=$parentFragment")
        dialogEvents = try {
            // Is there a parent fragment? If so, that will be what we call back
            parentFragment as DialogEvents
        } catch (e: NullPointerException) { // Was TypeCastException in tutorial
            try {
                // No parent fragment, so call back the Activity instead
                context as DialogEvents
            } catch (e: ClassCastException) {
                // Activity doesn't implement the interface
                throw ClassCastException("Activity $context must implement AppDialog.DialogEvents interface")
            }
        } catch (e: ClassCastException) {
            // Parent fragment doesn't implement the interface
            throw ClassCastException("Fragment $parentFragment must implement AppDialog.DialogEvents interface")
        } catch (e: Exception) {
            throw Exception(" ${e.toString()}")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "onCreateDialog")
        val builder = AlertDialog.Builder(requireContext())

        val arguments = arguments // Smart Cast Hack
        val dialogId: Int
        val messageString: String?
        var positionStringId: Int
        var negativeStringId: Int

        if (arguments != null) {
            dialogId = arguments.getInt(DIALOG_ID)
            messageString = arguments.getString(DIALOG_MESSAGE)
            if (dialogId == 0 || messageString == null)
                throw IllegalArgumentException("$DIALOG_ID & $DIALOG_MESSAGE not present in bundle")

            positionStringId = arguments.getInt(DIALOG_POSITIVE_RID)
            if (positionStringId == 0)
                positionStringId = R.string.ok

            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID)
            if (negativeStringId == 0)
                negativeStringId = R.string.cancel


        } else {
            throw IllegalArgumentException("$DIALOG_ID & $DIALOG_MESSAGE not present in bundle")
        }

        return builder.setMessage(messageString)
            .setPositiveButton(positionStringId) { DialogInterface, which ->
                dialogEvents?.onPositiveDialogResult(dialogId, arguments)
            }
            .setNegativeButton(negativeStringId) { DialogInterface, which ->
                // callback negative function, if you want to implement it
                //dialogEvents?.onNegativeDialogResult(dialogId, arguments)
            }
            .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        // back button or screen outside dialog
        Log.d(TAG, "onCancel")

    }

//    override fun onDismiss(dialog: DialogInterface) {
//        Log.d(TAG, "onDismiss")
//        super.onDismiss(dialog)
//        //dialogEvents?.onDialogCancelled(dialog)
//    }

    override fun onDetach() {
        Log.d(TAG, "onDetach")
        super.onDetach()
        dialogEvents = null // Reset active callback
    }
}