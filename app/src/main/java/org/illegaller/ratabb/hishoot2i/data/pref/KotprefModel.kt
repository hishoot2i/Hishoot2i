@file:Suppress("NOTHING_TO_INLINE")

package org.illegaller.ratabb.hishoot2i.data.pref

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.chibatching.kotpref.KotprefModel
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import kotlin.reflect.KProperty0

@JvmOverloads
inline fun <T> KotprefModel.asFlowable(property: KProperty0<T>, key: String? = null): Flowable<T> =
    Flowable.create<T>(
        { emitter: FlowableEmitter<T> ->
            val listenKey = key ?: property.name
            val listener =
                OnSharedPreferenceChangeListener { _: SharedPreferences?, changeKey: String? ->
                    if (listenKey == changeKey && !emitter.isCancelled) {
                        emitter.onNext(property.get())
                    }
                }
            emitter.setCancellable { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
            preferences.registerOnSharedPreferenceChangeListener(listener)
        },
        LATEST
    )
