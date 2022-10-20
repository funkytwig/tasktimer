package com.funkytwig.tasktimer

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.funkytwig.tasktimer.databinding.ActivityMainBinding

private const val TAG = "MainActivityXX"
private const val DIALOG_ID_CANCEL_EDIT = 1

class MainActivity : AppCompatActivity(), AddEditFragment.OnSaveClicked, MainFragment.OnTaskEdit {
    private var mTwoPain = false // are we in two pain mode (tablet/landscape)

    //    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val func = "onCreate"
        Log.d(TAG, func)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Get up two pain display
        mTwoPain = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if (fragment != null) {// we have a fragment
            // There was an existing fragment to edit/add a task so make sure pains are set up correctly
            showEditPain()
        } else { // no fragment
            binding.contentMain.taskDetailsContainer.visibility =
                if (mTwoPain) View.INVISIBLE else View.GONE
            binding.contentMain.mainFragment.visibility = View.VISIBLE
        }
        Log.d(TAG, "$func done")
    }

    private fun showEditPain() {
        Log.d(TAG, "showEditPain")
        binding.contentMain.taskDetailsContainer.visibility = View.VISIBLE // Show frame layout
        // hide left pain if in single pain view
        binding.contentMain.mainFragment.visibility = if (mTwoPain) View.VISIBLE else View.GONE
    }

    private fun removeEditPane(fragment: Fragment? = null) {
        Log.d(TAG, "removeEditPane")
        // This will work instead of passing fragment as arg but we will always have a reference
        // to fragment before removing it unless Save button it tapped so makes sense to pass it in
        // var fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
        // Set visibility of right-hand pane
        binding.contentMain.taskDetailsContainer.visibility =
            if (mTwoPain) View.INVISIBLE else View.GONE // GONE does not reserve space in display
        // Show left-hand pane
        binding.contentMain.mainFragment.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSaveClicked() {
        Log.d(TAG, "onSaveClicked")
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        removeEditPane(fragment)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected")
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d(TAG, item.itemId.toString())
        when (item.itemId) {
            R.id.menumain_addTask -> taskEditAdd(null)

            android.R.id.home -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
                if ((fragment is AddEditFragment) && fragment.isDirty()) // repeated code 3 down
                    showConformationDialogue(
                        DIALOG_ID_CANCEL_EDIT,
                        getString(R.string.cancelEnigDiag_message),
                        R.string.cancelEditDiag_positive_caption,
                        R.string.cancelEditDiag_negative_caption
                    )
                else removeEditPane(fragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // doing it like this rather tan renaming taskEditAdd to onTaskEdit so we get error if null passed
    override fun onTaskEdit(task: Task) {
        taskEditAdd(task)
    }

    private fun taskEditAdd(task: Task?) {
        val func = "taskEditAdd"
        Log.d(TAG, func)
        val newFragment = AddEditFragment.newInstance(task)
        supportFragmentManager.beginTransaction()
            .replace(R.id.task_details_container, newFragment)
            .commit()
        showEditPain()
        Log.d(TAG, "$func done")
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if (fragment == null || mTwoPain) {
            super.onBackPressed()
        } else {
            if ((fragment is AddEditFragment) && fragment.isDirty()) // repeated code 3 up
                showConformationDialogue(
                    DIALOG_ID_CANCEL_EDIT,
                    getString(R.string.cancelEnigDiag_message),
                    R.string.cancelEditDiag_positive_caption,
                    R.string.cancelEditDiag_negative_caption
                )
            else removeEditPane(fragment)
        }
    }
}