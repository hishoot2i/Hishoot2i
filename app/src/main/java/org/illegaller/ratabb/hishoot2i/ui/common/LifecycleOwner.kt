package org.illegaller.ratabb.hishoot2i.ui.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

inline fun <T : Any> Fragment.viewObserve(liveData: LiveData<T>, crossinline consume: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner) { consume(it) }
}
