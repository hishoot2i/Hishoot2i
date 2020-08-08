package org.illegaller.ratabb.hishoot2i.ui.common

interface Mvp {
    interface View {
        fun onError(e: Throwable)
    }

    interface Presenter<V : View> {
        fun attachView(view: V)
        fun detachView()
    }
}
