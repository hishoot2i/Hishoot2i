package org.illegaller.ratabb.hishoot2i.ui.common

import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BasePresenter<VIEW : Mvp.View> : Mvp.Presenter<VIEW> {
    private var view: VIEW? = null
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + job + CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable)
        }

    protected fun requiredView(): VIEW =
        requireNotNull(view) { "Do call attachView(View) before requesting this." }

    @CallSuper
    override fun attachView(view: VIEW) {
        this.view = view
        this.job = SupervisorJob()
    }

    @CallSuper
    override fun detachView() {
        this.job.cancel()
        this.view = null
    }
}
