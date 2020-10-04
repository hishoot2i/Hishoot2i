package pref.impl

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import pref.Pref
import pref.ext.editThenApply
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class EnumPref<T : Enum<*>> @JvmOverloads constructor(
    enumClass: KClass<T>,
    private val get: SharedPreferences.(String, Array<T>) -> T,
    private val put: Editor.(String, T) -> Unit,
    private val key: String? = null
) {
    private val enumConst = requireNotNull(enumClass.java.enumConstants)
    operator fun getValue(pref: Pref, prop: KProperty<*>): T =
        get(pref.preferences, key ?: prop.name, enumConst)

    operator fun setValue(pref: Pref, prop: KProperty<*>, value: T) {
        pref.editThenApply { put(it, key ?: prop.name, value) }
    }
}
