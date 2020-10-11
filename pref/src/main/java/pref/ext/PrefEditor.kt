package pref.ext

import android.content.SharedPreferences
import pref.Pref

internal inline fun Pref.editThenApply(
    crossinline block: (SharedPreferences.Editor) -> Unit
) = preferences.edit().also { block(it) }.apply()
