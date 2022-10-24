package com.funkytwig.tasktimer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.funkytwig.tasktimer.databinding.SettingsDialogBinding
import java.util.*
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.widget.SeekBar

private const val TAG = "SettingsDialogXX"

private const val SETTINGS_FIRST_DAY_OF_WEEK = "FirstDat"
private const val SETTINGS_IGNORE_LESS_THAN = "IgnoreLessThan"
private const val SETTINGS_DEFAULT_IGNORE_LESS_THAN: Int = 0

// TODO use preference library https://medium.com/google-developer-experts/exploring-android-jetpack-preferences-8bcb0b7bdd14

class SettingsDialog : AppCompatDialogFragment() {
    private lateinit var binding: SettingsDialogBinding

    //  0  1  2   3   4   5   6   7   8   9   10  11  12
    //  13   14   15   16   17   18   19   20   21   22   23    24
    private val deltas = intArrayOf(
        0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60,
        120, 180, 240, 300, 360, 420, 480, 540, 600, 900, 1800, 2700
    )

    private val defaultFirstDayOfWeek = GregorianCalendar(Locale.getDefault()).firstDayOfWeek
    private var firstDay = defaultFirstDayOfWeek
    private var ignoreLessThan = SETTINGS_DEFAULT_IGNORE_LESS_THAN

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setStyle(AppCompatDialogFragment.STYLE_NORMAL, R.style.SettingsDialogStyle)
        retainInstance = true // TODO: Depreciated
    }

    override fun onCreateView( // Using onCreateView as we need to access individual widgets
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        binding = SettingsDialogBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        dialog?.setTitle(R.string.action_settings)  // Same resource as for menu

        binding.ignoreSeconds.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) { // remove ?
                if (deltas[progress] < 60) { // TODO: repeated code, use function
                    binding.ignoreSecondsTitle.text =
                        getString(
                            R.string.settingsIgnoreSecondsTitle,
                            deltas[progress],
                            resources.getQuantityString(
                                R.plurals.settingsLittleUnits,
                                deltas[progress]
                            )
                        )
                } else {
                    val minutes = deltas[progress] / 60
                    binding.ignoreSecondsTitle.text =
                        getString(
                            R.string.settingsIgnoreSecondsTitle,
                            minutes,
                            resources.getQuantityString(R.plurals.settingsBigUnits, minutes)
                        )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // we don't need this
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // we don't need this
            }
        })

        binding.okButton.setOnClickListener {
            Log.d(TAG, "onListner OK Pressed")
            saveValues()
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            Log.d(TAG, "onListner Cancel Pressed")
            dismiss()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState == null) { // Don't do this when device rotated (we set retainInstance)
            readValues() // Cant call in onViewCreated as they will get overwritten when this fun called
            Log.d(TAG, "onViewStateRestored new view so retreve params")

            binding.firstDaySpinner.setSelection(firstDay - GregorianCalendar.SUNDAY) // // spinner values are zero-based

            val seekBarValue = deltas.binarySearch(ignoreLessThan) // relies on array being in order
            if (seekBarValue < 0) throw IndexOutOfBoundsException("$seekBarValue not found in deltas array")
            binding.ignoreSeconds.max =
                deltas.size - 1 // if delta made bigger layout would be wrong
            binding.ignoreSeconds.progress = seekBarValue

            // update ignoreSecondsTitle TODO: repeated code, use function
            if (ignoreLessThan < 60) {
                binding.ignoreSecondsTitle.text =
                    getString(
                        R.string.settingsIgnoreSecondsTitle,
                        ignoreLessThan,
                        resources.getQuantityString(R.plurals.settingsLittleUnits, ignoreLessThan)
                    )
            } else {
                val minutes = ignoreLessThan / 60
                binding.ignoreSecondsTitle.text =
                    getString(
                        R.string.settingsIgnoreSecondsTitle,
                        minutes,
                        resources.getQuantityString(R.plurals.settingsBigUnits, minutes)
                    )
            }
        }
    }

    private fun readValues() {
        with(getDefaultSharedPreferences(context)) {
            firstDay = getInt(SETTINGS_FIRST_DAY_OF_WEEK, defaultFirstDayOfWeek)
            ignoreLessThan = getInt(SETTINGS_IGNORE_LESS_THAN, SETTINGS_DEFAULT_IGNORE_LESS_THAN)
            Log.d(TAG, "readValues firstDay=$firstDay, ignoreLessThan=$ignoreLessThan")
        }
    }

    private fun saveValues() {
        val newFirstDay = // ( SUNDAY is probably 1, so it match calender class)
            binding.firstDaySpinner.selectedItemPosition + GregorianCalendar.SUNDAY
        val newIgnoreLessThan = deltas[binding.ignoreSeconds.progress] // getProgress method

        with(getDefaultSharedPreferences(context).edit()) {
            // as well as generally good we need to only putInt if changes as we are going to
            // use a listner on the settings
            if (newFirstDay != firstDay) putInt(SETTINGS_FIRST_DAY_OF_WEEK, newFirstDay)
            if (newIgnoreLessThan != ignoreLessThan)
                putInt(SETTINGS_IGNORE_LESS_THAN, newIgnoreLessThan)
            apply()
        }

        Log.d(TAG, "Saving newFirstDay=$newFirstDay, newIgnoreLessThan=$newIgnoreLessThan")
    }

    override fun onDestroy() {
        Log.d(TAG, "onViewCreated")
        super.onDestroy()
    }
}