package org.illegaller.ratabb.hishoot2i.ui.common.rx

import android.support.v7.widget.SearchView
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.MainThreadDisposable

object RxSearchView {
    @JvmStatic
    fun queryTextChange(searchView: SearchView): Observable<String> =
        Observable.create<String> { emitter: ObservableEmitter<String> ->
            MainThreadDisposable.verifyMainThread()
            val listener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean = false
                override fun onQueryTextChange(newText: String): Boolean {
                    if (!emitter.isDisposed) {
                        emitter.onNext(newText)
                        return true
                    }
                    return false
                }
            }
            emitter.setDisposable(object : MainThreadDisposable() {
                override fun onDispose() {
                    searchView.setOnQueryTextListener(null)
                }
            })
            searchView.setOnQueryTextListener(listener)
        }
}
