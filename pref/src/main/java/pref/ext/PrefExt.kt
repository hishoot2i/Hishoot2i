@file:Suppress("unused", "NOTHING_TO_INLINE")

package pref.ext

import android.content.SharedPreferences.Editor
import pref.Pref
import pref.impl.GenericNullablePref
import pref.impl.GenericPref

// region DEFAULT [booleanPref, floatPref, intPref, longPref, stringPref, stringSetPref]
@JvmOverloads
inline fun Pref.booleanPref(default: Boolean, key: String? = null) =
    GenericPref(
        key = key,
        get = { KEY -> getBoolean(KEY, default) },
        put = (Editor::putBoolean)
    )

@JvmOverloads
inline fun Pref.floatPref(default: Float, key: String? = null) =
    GenericPref(
        key = key,
        get = { KEY -> getFloat(KEY, default) },
        put = (Editor::putFloat)
    )

@JvmOverloads
inline fun Pref.intPref(default: Int, key: String? = null) =
    GenericPref(
        key = key,
        get = { KEY -> getInt(KEY, default) },
        put = (Editor::putInt)
    )

@JvmOverloads
inline fun Pref.longPref(default: Long, key: String? = null) =
    GenericPref(
        key = key,
        get = { KEY -> getLong(KEY, default) },
        put = (Editor::putLong)
    )

@JvmOverloads
inline fun Pref.stringPref(default: String, key: String? = null) =
    GenericPref(
        key = key,
        get = { KEY -> getString(KEY, default) ?: default },
        put = (Editor::putString)
    )

@JvmOverloads
inline fun Pref.stringSetPref(default: Set<String>, key: String? = null) =
    GenericPref<Set<String>>(
        key = key,
        get = { KEY -> getStringSet(KEY, default) ?: default },
        put = (Editor::putStringSet)
    )
// endregion

// region OPTIONAL [booleanPref, floatPref, intPref, longPref, stringPref, stringSetPref]
@JvmOverloads
inline fun Pref.booleanPref(key: String? = null) =
    GenericNullablePref(
        key = key,
        get = { KEY, EXIST -> if (EXIST) getBoolean(KEY, false) else null },
        put = (Editor::putBoolean)
    )

@JvmOverloads
inline fun Pref.floatPref(key: String? = null) =
    GenericNullablePref(
        key = key,
        get = { KEY, EXIST -> if (EXIST) getFloat(KEY, -1F) else null },
        put = (Editor::putFloat)
    )

@JvmOverloads
inline fun Pref.intPref(key: String? = null) =
    GenericNullablePref(
        key = key,
        get = { KEY, EXIST -> if (EXIST) getInt(KEY, -1) else null },
        put = (Editor::putInt)
    )

@JvmOverloads
inline fun Pref.longPref(key: String? = null) =
    GenericNullablePref(
        key = key,
        get = { KEY, EXIST -> if (EXIST) getLong(KEY, -1L) else null },
        put = (Editor::putLong)
    )

@JvmOverloads
inline fun Pref.stringPref(key: String? = null) =
    GenericNullablePref(
        key = key,
        get = { KEY, EXIST -> if (EXIST) getString(KEY, null) else null },
        put = (Editor::putString)
    )

@JvmOverloads
inline fun Pref.stringSetPref(key: String? = null) =
    GenericNullablePref<Set<String>>(
        key = key,
        get = { KEY, EXIST -> if (EXIST) getStringSet(KEY, null) else null },
        put = (Editor::putStringSet)
    )
// endregion

fun Pref.clear() {
    editThenApply(Editor::clear)
}
