package org.illegaller.ratabb.hishoot2i.ui.common

import android.support.annotation.CallSuper

abstract class BasePresenter<VIEW : Mvp.View> : Mvp.Presenter<VIEW> {
    protected var view: VIEW? = null
        get() {
            if (field == null) throw MvpNotAttachedViewException()
            return field
        }

    @CallSuper
    override fun attachView(view: VIEW) {
        this.view = view
    }

    @CallSuper
    override fun detachView() {
        this.view = null
    }
}