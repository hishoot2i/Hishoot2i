package entity

import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import entity.DayNightMode.DARK
import entity.DayNightMode.LIGHT
import entity.DayNightMode.SYSTEM

inline val DayNightMode.mode: Int
    get() = when (this) {
        LIGHT -> MODE_NIGHT_NO
        DARK -> MODE_NIGHT_YES
        SYSTEM -> {
            if (SDK_INT >= 28) MODE_NIGHT_FOLLOW_SYSTEM
            else MODE_NIGHT_AUTO_BATTERY
        }
    }
