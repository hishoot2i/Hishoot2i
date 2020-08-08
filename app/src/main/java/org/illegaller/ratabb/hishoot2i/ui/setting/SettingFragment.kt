package org.illegaller.ratabb.hishoot2i.ui.setting

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {
    @Inject
    lateinit var appPref: AppPref

    private lateinit var appThemeDarkSwitch: SwitchCompat
    private lateinit var badgeSystemFontSwitch: SwitchCompat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            appThemeDarkSwitch = findViewById(R.id.setting_app_themes_dark)
            badgeSystemFontSwitch = findViewById(R.id.setting_badge_system_font)
        }
        //
        with(appThemeDarkSwitch) {
            isChecked = appPref.appThemesDarkEnable
            setOnCheckedChangeListener { cb: CompoundButton, isChecked: Boolean ->
                cb.preventMultipleClick {
                    if (appPref.appThemesDarkEnable != isChecked) {
                        appPref.appThemesDarkEnable = isChecked
                        (activity as? AppCompatActivity)?.recreate()
                    }
                }
            }
        }
        //
        with(badgeSystemFontSwitch) {
            isChecked = appPref.systemFontEnable
            setOnCheckedChangeListener { cb: CompoundButton, isChecked: Boolean ->
                cb.preventMultipleClick {
                    if (appPref.systemFontEnable != isChecked) {
                        appPref.systemFontEnable = isChecked
                    }
                }
            }
        }
    }
}
