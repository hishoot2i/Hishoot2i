package org.illegaller.ratabb.hishoot2i.ui.common

import kotlinx.coroutines.CoroutineScope

interface Mvp {
    interface View {
        fun onError(e: Throwable)
    }

    interface Presenter<V : View> : CoroutineScope {
        fun attachView(view: V)
        fun detachView()
    }
}
