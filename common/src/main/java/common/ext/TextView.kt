@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.widget.TextViewCompat
import android.text.InputFilter
import android.view.KeyEvent
import android.widget.TextView
import common.ext.graphics.createVectorDrawableTint

inline fun TextView.onEditorAction(crossinline consume: (actionId: Int) -> Boolean) {
    setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? -> consume(actionId) }
}

inline fun TextView.addInputFilter(vararg filter: InputFilter) {
    filters += filter
}

@JvmOverloads
inline fun TextView.compoundVectorDrawables(
    @DrawableRes start: Int = 0,
    @DrawableRes top: Int = 0,
    @DrawableRes end: Int = 0,
    @DrawableRes bottom: Int = 0,
    @ColorRes tint: Int
) {
    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
        this,
        context.createVectorDrawableTint(start, tint),
        context.createVectorDrawableTint(top, tint),
        context.createVectorDrawableTint(end, tint),
        context.createVectorDrawableTint(bottom, tint)
    )
}