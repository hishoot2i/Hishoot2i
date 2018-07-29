@file:Suppress("NOTHING_TO_INLINE")

package org.illegaller.ratabb.hishoot2i.data.rx

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import rbb.hishoot2i.common.ext.DEFAULT_DELAY_MS
import java.util.concurrent.TimeUnit.MILLISECONDS

// Schedule
inline fun <T> Flowable<T>.ioUI(schedule: SchedulerProvider): Flowable<T> =
    compose { subscribeOn(schedule.io()).observeOn(schedule.ui()) }

inline fun <T> Single<T>.computationUI(schedule: SchedulerProvider): Single<T> =
    compose { subscribeOn(schedule.computation()).observeOn(schedule.ui()) }

inline fun <T> Single<T>.ioUI(schedule: SchedulerProvider): Single<T> =
    compose { subscribeOn(schedule.io()).observeOn(schedule.ui()) }

// FIXME: ?
@JvmOverloads
inline fun <T> Flowable<T>.delayed(
    durationOnMillis: Long = DEFAULT_DELAY_MS
): Flowable<T> = throttleLatest(durationOnMillis, MILLISECONDS)
    .debounce(durationOnMillis, MILLISECONDS)
    .distinctUntilChanged()

@JvmOverloads
inline fun <T> Observable<T>.delayed(
    durationOnMillis: Long = DEFAULT_DELAY_MS
): Observable<T> = throttleLatest(durationOnMillis, MILLISECONDS)
    .debounce(durationOnMillis, MILLISECONDS)
    .distinctUntilChanged()
