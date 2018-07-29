package org.illegaller.ratabb.hishoot2i.data

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import io.reactivex.Flowable
import io.reactivex.flowables.ConnectableFlowable
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.imageloader.ImageLoader
import rbb.hishoot2i.common.imageloader.uil.UilImageLoaderImpl
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/* Initialized UilImageLoaderImpl#DiskCache at I/O (Background) thread. */
@Suppress("CheckResult") //
class ImageLoaderWrapper @Inject constructor(
    context: Context,
    schedulerProvider: SchedulerProvider
) : ImageLoader {
    private lateinit var wrap: ImageLoader
    private val isInitialized = AtomicBoolean()
    private val ioConnection: ConnectableFlowable<ImageLoader> =
        Flowable.fromCallable<ImageLoader> { UilImageLoaderImpl(context, false/**/) }
            .ioUI(schedulerProvider)
            .publish()

    init {
        ioConnection.subscribe {
            this.wrap = it
            isInitialized.set(true)
        }
        ioConnection.connect()
    }

    override fun display(imageView: ImageView, source: String) {
        ensureInitialized()
        wrap.display(imageView, source)
    }

    override fun loadSync(source: String, isSave: Boolean): Bitmap? {
        ensureInitialized()
        return wrap.loadSync(source, isSave)
    }

    override fun loadSync(source: String, reqSizes: Sizes?, isSave: Boolean): Bitmap? {
        ensureInitialized()
        return wrap.loadSync(source, reqSizes, isSave)
    }

    override fun clearMemoryCache() {
        ensureInitialized()
        wrap.clearMemoryCache()
    }

    override fun clearDiskCache() {
        ensureInitialized()
        wrap.clearDiskCache()
    }

    override fun totalDiskCacheSize(): Long {
        ensureInitialized()
        return wrap.totalDiskCacheSize()
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