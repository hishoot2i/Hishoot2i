@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

inline fun ViewGroup.forEach(action: (view: View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}

inline fun ViewGroup.forEachIndex(action: (index: Int, view: View) -> Unit) {
    for (index in 0 until childCount) {
        action(index, getChildAt(index))
    }
}

inline fun ViewGroup.inflateNotAttach(@LayoutRes resource: Int): View =
    layoutInflater.inflate(resource, this, false)
