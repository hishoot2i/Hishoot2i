package org.illegaller.ratabb.hishoot2i.ui.setting

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomappbar.BottomAppBar
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {
    @Inject
    lateinit var appPref: AppPref

    private lateinit var appDayNight: View
    private lateinit var appDayNightText: TextView
    private lateinit var settingBottomAppBar: BottomAppBar
    private lateinit var badgeSystemFontSwitch: SwitchCompat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            appDayNight = findViewById(R.id.setting_day_night)
            appDayNightText = findViewById(R.id.setting_day_night_text)
            badgeSystemFontSwitch = findViewById(R.id.setting_badge_system_font)
            settingBottomAppBar = findViewById(R.id.settingBottomAppBar)
        }
        settingBottomAppBar.setNavigationOnClickListener {
            it.preventMultipleClick { findNavController().popBackStack() }
        }
        appDayNight.setOnClickListener(::dayNightOnClick)

        updateDayNightUi()
        //
        with(badgeSystemFontSwitch) {
            isChecked = appPref.systemFontEnable
            setOnCheckedChangeListener(::badgeSystemFontOnCheck)
        }
    }

    private fun badgeSystemFontOnCheck(cb: CompoundButton, isChecked: Boolean) {
        cb.preventMultipleClick {
            if (appPref.systemFontEnable != isChecked) {
                appPref.systemFontEnable = isChecked
            }
        }
    }

    private fun dayNightOnClick(it: View) {
        it.preventMultipleClick {

            ThemeChooserDialog.newInstance(appPref.dayNightMode)
                .apply {
                    callback = { mode ->
                        appPref.dayNightMode = mode
                        AppCompatDelegate.setDefaultNightMode(mode)
                        updateDayNightUi()
                    }
                }
                .show(childFragmentManager)
        }
    }

    private fun updateDayNightUi() {
        when (appPref.dayNightMode) {
            MODE_NIGHT_FOLLOW_SYSTEM -> R.string.follow_system
            MODE_NIGHT_YES -> R.string.dark
            MODE_NIGHT_NO -> R.string.light
            else -> android.R.string.unknownName
        }.let {
            appDayNightText.setText(it)
        }
    }
}
