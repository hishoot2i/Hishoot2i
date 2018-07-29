package org.illegaller.ratabb.hishoot2i.data

import android.content.Context
import io.reactivex.Flowable
import io.reactivex.flowables.ConnectableFlowable
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import rbb.hishoot2i.common.FileConstants
import rbb.hishoot2i.common.FileConstantsImpl
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@Suppress("CheckResult")
class FileConstantsWrapper @Inject constructor(
    context: Context,
    schedulerProvider: SchedulerProvider
) : FileConstants {
    private lateinit var wrap: FileConstants
    private val isInitialized = AtomicBoolean()
    private val ioConnection: ConnectableFlowable<FileConstants> =
        Flowable.fromCallable<FileConstants> { FileConstantsImpl(context) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.io())
            .publish()

    init {
        ioConnection.subscribe {
            this.wrap = it
            isInitialized.set(true)
        }
        ioConnection.connect()
    }

    override fun savedDir(): File {
        ensureInitialized()
        return wrap.savedDir()
    }

    override fun htzDir(): File {
        ensureInitialized()
        return wrap.htzDir()
    }

    override fun bgCrop(): File {
        ensureInitialized()
        return wrap.bgCrop()
    }

    @Synchronized
    private fun ensureInitialized() {
        synchronized(this) {
            if (!isInitialized.getAndSet(true)) {
                ioConnection.subscribe { this.wrap = it }
            }
        }
    }
}