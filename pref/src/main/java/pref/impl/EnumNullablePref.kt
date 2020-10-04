package pref.impl

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import pref.Pref
import pref.ext.editThenApply
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class EnumNullablePref<T : Enum<*>> @JvmOverloads constructor(
    enumClass: KClass<T>,
    private val get: SharedPreferences.(String, Boolean, Array<T?>) -> T?,
    private val put: Editor.(String, T) -> Unit,
    private val key: String? = null
) {
    private val enumConst = requireNotNull(enumClass.java.enumConstants)
    operator fun getValue(pref: Pref, prop: KProperty<*>): T? {
        val noNullKey = key ?: prop.name
        return get(
            pref.preferences,
            noNullKey,
            pref.preferences.contains(noNullKey),
            enumConst
        )
    }

    operator fun setValue(pref: Pref, prop: KProperty<*>, value: T?) {
        val noNullKey = key ?: prop.name
        pref.editThenApply { editor ->
            value?.let { put(editor, noNullKey, it) } ?: editor.remove(noNullKey)
        }
    }
}
