@file:Suppress("SpellCheckingInspection", "NOTHING_TO_INLINE")

package pref.ext

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import pref.Pref
import kotlin.reflect.KProperty0

@ExperimentalCoroutinesApi
@JvmOverloads
inline fun <T> Pref.asFlow(
    prop: KProperty0<T>,
    key: String? = null
): Flow<T> = callbackFlow {
    val listenKey = key ?: prop.name
    val listener = OnSharedPreferenceChangeListener { _, changeKey ->
        if (listenKey == changeKey && !isClosedForSend) this.sendBlocking(prop.get())
    }
    preferences.registerOnSharedPreferenceChangeListener(listener)
    awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
}
