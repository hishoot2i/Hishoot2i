@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS

/**
 * @see[View.getVisibility]
 * @see[View.setVisibility]
 */
inline var View.isVisible: Boolean
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }

/**
 * @see [Context.getSystemService]
 * @see [InputMethodManager.hideSoftInputFromWindow]
 */
inline fun View.hideSoftKey() {
    (context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(windowToken, HIDE_NOT_ALWAYS)
}

inline fun View.onKey(crossinline consume: (keyCode: Int, event: KeyEvent) -> Boolean) {
    setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent -> consume(keyCode, event) }
}

inline val View.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(context)

inline fun View.preventMultipleClick(block: () -> Unit) {
    if (!isEnabled) return
    isEnabled = false
    block()
    postDelayed { isEnabled = true }
}

inline fun MenuItem.preventMultipleClick(block: () -> Boolean): Boolean {
    if (!isEnabled) return false
    isEnabled = false
    postDelayed { isEnabled = true }
    return block()
}
