package com.funkytwig.tasktimer

import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.funkytwig.tasktimer.databinding.ActivityMainBinding

private const val TAG = "MainActivityXX"

class MainActivity : AppCompatActivity(), AddEditFragment.OnSaveClicked {
    private var mTwoPain = false // are we in two pain mode (tablet/landscape) NEW

    //    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val func = "onCreate"
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Get up two pain display
        mTwoPain = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container) // NEW
        if (fragment != null) {// we have a fragment
            // There was an existing fragment to edit/add a task so make sure pains are set up correctly
            showEditPain()
        } else { // no fragment
            binding.contentMain.taskDetailsContainer.visibility =
                if (mTwoPain) View.INVISIBLE else View.GONE
            binding.contentMain.mainFragment.visibility = View.VISIBLE
        }
    }

    private fun showEditPain() {
        Log.d(TAG, "showEditPain")
        binding.contentMain.taskDetailsContainer.visibility = View.VISIBLE // Show frame layout
        // hide left pain if in single pain view
        binding.contentMain.mainFragment.visibility = if (mTwoPain) View.VISIBLE else View.GONE
    }

    private fun removeEditPane(fragment: Fragment? = null) { // NEW
        Log.d(TAG, "removeEditPane")
        // This will work instead of passing fragment as arg but we will always have a reference
        // to fragment before removing it unless Save button it tapped so makes sence to pass it in
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
    }

    override fun onSaveClicked() {
        Log.d(TAG, "onSaveClicked")
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container) // NEW
        removeEditPane(fragment) // NEW
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menumain_addTask -> taskEditAdd(null) // NEW
            // R.id.menumain_settings -> true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun taskEditAdd(task: Task?) { // NEW
        val func = "taskEditAdd"
        Log.d(TAG, func)
        val newFragment = AddEditFragment.newInstance(task)
        supportFragmentManager.beginTransaction()
            .replace(R.id.task_details_container, newFragment)
            .commit()
        showEditPain()
        Log.d(TAG, "$func done")
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.fragment)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }

    // ** From here its just logging functions **

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "onRestoreInstanceState")
        super.onRestoreInstanceState(savedInstanceState)
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
}