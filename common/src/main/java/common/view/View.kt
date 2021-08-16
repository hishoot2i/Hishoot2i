@file:Suppress("NOTHING_TO_INLINE")

package common.view

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import androidx.core.os.postDelayed
import androidx.core.view.postDelayed

/**
 * @see [Context.getSystemService]
 * @see [InputMethodManager.hideSoftInputFromWindow]
 */
inline fun View.hideSoftKey() {
    (context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(windowToken, HIDE_NOT_ALWAYS)
}

inline fun View.onKey(crossinline consume: (keyCode: Int, event: KeyEvent) -> Boolean) {
    setOnKeyListener { _, keyCode, event -> consume(keyCode, event) }
}

inline val View.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(context)

inline fun View.preventMultipleClick(block: () -> Unit) {
    if (!isEnabled) return
    isEnabled = false
    block()
    postDelayed(600L) { isEnabled = true }
}

@Suppress("DEPRECATION")
inline fun MenuItem.preventMultipleClick(block: () -> Boolean): Boolean {
    if (!isEnabled) return false
    isEnabled = false
    Handler().postDelayed(600L) { isEnabled = true }
    return block()
}

@Suppress("DEPRECATION")
const val EDGE_TO_EDGE_FLAGS =
    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE

@Suppress("DEPRECATION")
inline fun View.setSystemUiFlagEdgeToEdge(enabled: Boolean) {
    systemUiVisibility = systemUiVisibility and EDGE_TO_EDGE_FLAGS.inv() or
        if (enabled) EDGE_TO_EDGE_FLAGS else 0
}
