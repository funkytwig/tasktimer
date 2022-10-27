package com.funkytwig.tasktimer

// TODO: Think we lost some functions from this file

import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.showConformationDialogue(
    id: Int,
    message: String,
    positiveCaption: Int = R.string.ok,
    negativeCaption: Int = R.string.cancel
) {
    val args = Bundle().apply {
        putInt(DIALOG_ID, id)
        putString(DIALOG_MESSAGE, message)
        putInt(DIALOG_POSITIVE_RID, positiveCaption)
        putInt(DIALOG_NEGATIVE_RID, negativeCaption)
    }
    val dialog = AppDialog()
    dialog.arguments = args
    dialog.show(supportFragmentManager, null)
}

fun ContentValues.toString(values: ContentValues?): String {
    if (values == null) return "null"
    var output: String = ""
    for (key in values.keySet()) output += "$key=${values.get(key)} | "
    if (output == "") return "ContentValues has no elements"
    return "ContentValues:$output"
}