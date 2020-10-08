@file:Suppress("SpellCheckingInspection", "NOTHING_TO_INLINE")

package pref.ext

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import pref.Pref
import kotlin.reflect.KProperty0

@JvmOverloads
inline fun <T> Pref.asFlowable(
    prop: KProperty0<T>,
    key: String? = null
) = Flowable.create<T>(
    { emitter ->
        val listenKey = key ?: prop.name
        val listener = OnSharedPreferenceChangeListener { _, changeKey ->
            if (listenKey == changeKey && !emitter.isCancelled) emitter.onNext(prop.get())
        }
        emitter.setCancellable { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
        preferences.registerOnSharedPreferenceChangeListener(listener)
    },
    BackpressureStrategy.LATEST
)