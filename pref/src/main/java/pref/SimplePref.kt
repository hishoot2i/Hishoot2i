package pref

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

@Suppress("unused")
abstract class SimplePref : Pref {

    final override val preferences: SharedPreferences

    constructor(preferences: SharedPreferences) {
        this.preferences = preferences
    }

    constructor(context: Context) : this(
        context, "${context.packageName}_preferences"
    )

    constructor(context: Context, preferencesName: String) : this(
        context.getSharedPreferences(preferencesName, MODE_PRIVATE)
    )
}
