package pref.impl

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import pref.Pref
import pref.ext.editThenApply
import kotlin.reflect.KProperty

class GenericPref<T : Any> @JvmOverloads constructor(
    private val get: SharedPreferences.(String) -> T,
    private val put: Editor.(String, T) -> Unit,
    private val key: String? = null
) {
    operator fun getValue(pref: Pref, prop: KProperty<*>): T =
        get(pref.preferences, key ?: prop.name)

    operator fun setValue(pref: Pref, prop: KProperty<*>, value: T) {
        pref.editThenApply { put(it, key ?: prop.name, value) }
    }
}
