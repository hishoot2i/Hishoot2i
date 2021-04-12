@file:Suppress("NOTHING_TO_INLINE")

package common.widget

import android.text.InputFilter
import android.view.KeyEvent
import android.widget.TextView

inline fun TextView.onEditorAction(crossinline consume: (actionId: Int) -> Boolean) {
    setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? -> consume(actionId) }
}

inline fun TextView.addInputFilter(vararg filter: InputFilter) {
    filters += filter
}
