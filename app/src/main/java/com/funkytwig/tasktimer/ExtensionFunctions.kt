package com.funkytwig.tasktimer

// TODO: Think we lost some functions from this file

import android.app.Application
import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.AndroidViewModel

fun FragmentActivity.findFragmentById(id: Int): Fragment? {
    return supportFragmentManager.findFragmentById(id)
}
//fun AndroidViewModel.resource(stringId : Int, params: Any) : String {
//    return getApplication<Application>().resources.getString(
//        stringId, params
//    )
//}

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

/**
 * Extensions based on an article by Dinesh Babuhunky
 * at https://medium.com/thoughts-overflow/how-to-add-a-fragment-in-kotlin-way-73203c5a450b
 */

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun FragmentActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}

fun FragmentActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { replace(frameId, fragment) }
}

fun FragmentActivity.removeFragment(fragment: Fragment) {
    supportFragmentManager.inTransaction { remove(fragment) }
}

fun ContentValues.toString(values: ContentValues?): String {
    if (values == null) return "null"
    var output: String = ""
    for (key in values.keySet()) output += "$key=${values.get(key)} | "
    if (output == "") return "ContentValues has no elements"
    return "ContentValues:$output"
}

fun Array<String?>.toString(array: Array<String>?): String {
    if (array == null) return "null"
    var output: String = ""
    for (element in array) output += "$element 1"
    if (output == "") return "ContentValues has no elements"
    return "Array:$output"
}
