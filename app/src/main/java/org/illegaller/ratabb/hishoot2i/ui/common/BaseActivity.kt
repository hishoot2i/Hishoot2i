package org.illegaller.ratabb.hishoot2i.ui.common

import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Bundle
import common.ext.graphics.color
import common.ext.taskDescription
import dagger.android.support.DaggerAppCompatActivity
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import javax.inject.Inject

@SuppressLint("Registered")
open class BaseActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var appPref: AppPref
    private var currentTheme: Int = R.style.AppTheme
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentTheme = appThemeFromPref()
        setTheme(currentTheme)

        if (SDK_INT >= LOLLIPOP) taskDescription(colorPrimary = color(R.color.primaryDark))
    }

    override fun onResume() {
        if (currentTheme != appThemeFromPref()) recreate()
        super.onResume()
    }

    private fun appThemeFromPref(): Int = if (appPref.appThemesDarkEnable) R.style.AppTheme
    else R.style.AppTheme_Light
}