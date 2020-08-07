package org.illegaller.ratabb.hishoot2i.ui.common

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    /* @Inject
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
     else R.style.AppTheme_Light*/
}