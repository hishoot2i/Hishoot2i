package entity

import android.os.Build.VERSION.SDK_INT
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import common.ext.exhaustive
import entity.DayNightMode.DARK
import entity.DayNightMode.LIGHT
import entity.DayNightMode.SYSTEM
import org.illegaller.ratabb.hishoot2i.R.id.themeDarkRb
import org.illegaller.ratabb.hishoot2i.R.id.themeLightRb
import org.illegaller.ratabb.hishoot2i.R.id.themeSysDefRb

inline val DayNightMode.mode: Int
    get() = when (this) {
        LIGHT -> MODE_NIGHT_NO
        DARK -> MODE_NIGHT_YES
        SYSTEM -> {
            if (SDK_INT >= 28) MODE_NIGHT_FOLLOW_SYSTEM
            else MODE_NIGHT_AUTO_BATTERY
        }
    }.exhaustive

@get:IdRes
inline val DayNightMode.resId: Int
    get() = when (this) {
        LIGHT -> themeLightRb
        DARK -> themeDarkRb
        SYSTEM -> themeSysDefRb
    }.exhaustive

fun DayNightMode.Companion.fromIdRes(@IdRes idRes: Int): DayNightMode = when (idRes) {
    themeLightRb -> LIGHT
    themeDarkRb -> DARK
    themeSysDefRb -> SYSTEM
    else -> SYSTEM // fallback
}
