@file:Suppress("unused", "NOTHING_TO_INLINE")

package pref.ext

import pref.Pref
import pref.impl.EnumNullablePref
import pref.impl.EnumPref

// region Enum Default [enumOrdinalPref, enumValuePref]
@JvmOverloads
inline fun <reified T : Enum<*>> Pref.enumOrdinalPref(default: T, key: String? = null) =
    EnumPref(
        enumClass = T::class,
        get = { KEY, CONSTANTS -> CONSTANTS[getInt(KEY, default.ordinal)] },
        put = { KEY, VALUE -> putInt(KEY, VALUE.ordinal) },
        key = key
    )

@JvmOverloads
inline fun <reified T : Enum<*>> Pref.enumValuePref(default: T, key: String? = null) =
    EnumPref(
        enumClass = T::class,
        get = { KEY, CONSTANTS ->
            CONSTANTS.first { enum -> enum.name == getString(KEY, default.name) }
        },
        put = { KEY, VALUE -> putString(KEY, VALUE.name) },
        key = key
    )
// endregion

// region Enum Optional [enumValuePref, enumOrdinalPref]
@JvmOverloads
inline fun <reified T : Enum<*>> Pref.enumValuePref(key: String? = null) =
    EnumNullablePref(
        enumClass = T::class,
        get = { KEY, EXIST, CONSTANTS ->
            if (EXIST) getString(KEY, null)?.let { name ->
                CONSTANTS.firstOrNull { enum -> enum?.name == name }
            }
            else null
        },
        put = { KEY, VALUE -> putString(KEY, VALUE.name) },
        key = key
    )

@JvmOverloads
inline fun <reified T : Enum<*>> Pref.enumOrdinalPref(key: String? = null) =
    EnumNullablePref(
        enumClass = T::class,
        get = { KEY, EXIST, CONSTANTS ->
            if (EXIST) getInt(KEY, -1).takeIf { it != -1 }?.let { ordinal ->
                CONSTANTS.firstOrNull { enum -> enum?.ordinal == ordinal }
            }
            else null
        },
        put = { KEY, VALUE -> putInt(KEY, VALUE.ordinal) },
        key = key
    )
// endregion
