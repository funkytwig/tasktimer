package com.funkytwig.tasktimer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.funkytwig.tasktimer.databinding.ActivityMainBinding
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.funkytwig.tasktimer.debug.TestData

private const val TAG = "MainActivityXX"
private const val DIALOG_ID_CANCEL_EDIT = 1

// TODO About menu not working in landscape

class MainActivity : AppCompatActivity(),
    AddEditFragment.OnSaveClicked, MainFragment.OnTaskEdit, AppDialog.DialogEvents {
    private var mTwoPain = false // are we in two pain mode (tablet/landscape)

    // module scope because we need to dismiss it in onStop (e.g. orientation change) to avoid memory leaks.
    private var aboutDialog: AlertDialog? = null

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
        val fragment = findFragmentById(R.id.task_details_container)
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
        if (fragment != null) removeFragment(fragment)
        // Set visibility of right-hand pane
        binding.contentMain.taskDetailsContainer.visibility =
            if (mTwoPain) View.INVISIBLE else View.GONE // GONE does not reserve space in display
        // Show left-hand pane
        binding.contentMain.mainFragment.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSaveClicked() {
        Log.d(TAG, "onSaveClicked")
        val fragment = findFragmentById(R.id.task_details_container)
        removeEditPane(fragment)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        if (BuildConfig.DEBUG) {
            val generate = menu.findItem(R.id.menumain_generate)
            generate.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menumain_addTask -> taskEditAdd(null)
            R.id.menumain_showDurations -> startActivity(Intent(this, DurationsReport::class.java))
            R.id.menumain_showAbout -> showAboutDialog()
            R.id.menumain_generate -> TestData.generateTextDate(contentResolver)
            R.id.menumain_settings -> {
                val dialog = SettingsDialog()
                dialog.show(supportFragmentManager, null)
            }
            android.R.id.home -> {
                val fragment = findFragmentById(R.id.task_details_container)
                if ((fragment is AddEditFragment) && fragment.isDirty()) { // repeated code 3 down
                    showConformationDialogue(
                        DIALOG_ID_CANCEL_EDIT,
                        getString(R.string.cancelEnigDiag_message),
                        R.string.cancelEditDiag_positive_caption,
                        R.string.cancelEditDiag_negative_caption
                    )
                } else removeEditPane(fragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showAboutDialog() {
        val messageView = layoutInflater.inflate(R.layout.about, null, false)
        val builder = AlertDialog.Builder(this)

        // Must be called before create.
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setPositiveButton(R.string.ok) { _, _ -> // add OK button
            if (aboutDialog != null && aboutDialog?.isShowing == true) aboutDialog?.dismiss()
        }

        aboutDialog = builder.setView(messageView).create()
        aboutDialog?.setCanceledOnTouchOutside(true) // default behaviour, important as no buttons

        // not ideal as con and title are not part of the view & has links
        // messageView.setOnClickListener{
        //     if (aboutDialog != null && aboutDialog?.isShowing == true) aboutDialog?.dismiss()
        // }

        // cant use synthetic import as we have to look for ID in messageView
        val aboutVersion = messageView.findViewById<TextView>(R.id.aboutVerison)
        aboutVersion.text = BuildConfig.VERSION_NAME

        // Use a nullable type: the TextView won't exist on API 21 and higher
        val aboutUrl: TextView? = messageView.findViewById(R.id.aboutUrl)
        aboutUrl?.setOnClickListener { v ->
            val intent = Intent(Intent.ACTION_VIEW)
            val s = (v as TextView).text.toString()
            intent.data = Uri.parse(s)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this@MainActivity, R.string.about_url_error, Toast.LENGTH_LONG)
                    .show()
            }
        }
        aboutDialog?.show()
    }


    // to stop above memory leaking when we rotate.  This si why we have aboutDialog as val.
    override fun onStop() {
        val func = "onStop"
        Log.d(TAG, func)
        super.onStop()
        if (aboutDialog?.isShowing == true) aboutDialog?.dismiss()
    }

    // doing it like this rather tan renaming taskEditAdd to onTaskEdit so we get error if null passed
    override fun onTaskEdit(task: Task) {
        taskEditAdd(task)
    }

    private fun taskEditAdd(task: Task?) {
        val func = "taskEditAdd"
        Log.d(TAG, func)
        replaceFragment(AddEditFragment.newInstance(task), R.id.task_details_container)
        showEditPain()
        Log.d(TAG, "$func done")
    }

    override fun onBackPressed() { // TODO: replace with non depreciated
        val fragment = findFragmentById(R.id.task_details_container)
        if (fragment == null || mTwoPain) {
            super.onBackPressed()
        } else {
            if ((fragment is AddEditFragment) && fragment.isDirty()) { // repeated code 3 up
                showConformationDialogue(
                    DIALOG_ID_CANCEL_EDIT,
                    getString(R.string.cancelEnigDiag_message),
                    R.string.cancelEditDiag_positive_caption,
                    R.string.cancelEditDiag_negative_caption
                )
            } else removeEditPane(fragment)
        }
    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle) {
        if (dialogId == DIALOG_ID_CANCEL_EDIT) {
            val fragment = findFragmentById(R.id.task_details_container)
            removeEditPane()
        } else throw RuntimeException("Dialog ID $dialogId not implemented")
    }
}