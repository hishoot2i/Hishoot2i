package org.illegaller.ratabb.hishoot2i.ui.common

import androidx.annotation.CallSuper

abstract class BasePresenter<VIEW : Mvp.View> : Mvp.Presenter<VIEW> {
    private var view: VIEW? = null

    protected fun requiredView(): VIEW =
        requireNotNull(view) { "Do call attachView(View) before requesting this." }

    @CallSuper
    override fun attachView(view: VIEW) {
        this.view = view
    }

    @CallSuper
    override fun detachView() {
        this.view = null
    }
}
