package org.illegaller.ratabb.hishoot2i.ui.common.rx

import androidx.appcompat.widget.SearchView
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter

object RxSearchView {
    @JvmStatic
    fun queryTextChange(searchView: SearchView): Observable<String> =
        Observable.create { emitter: ObservableEmitter<String> ->
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
