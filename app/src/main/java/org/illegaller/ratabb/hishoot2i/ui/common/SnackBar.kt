package org.illegaller.ratabb.hishoot2i.ui.common

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

@JvmOverloads
inline fun showSnackBar(
    view: View,
    @StringRes resId: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    @IdRes anchorViewId: Int = View.NO_ID,
    action: Snackbar.() -> Unit = {}
) = showSnackBar(
    view,
    view.resources.getText(resId),
    duration,
    anchorViewId,
    action
)

@JvmOverloads
inline fun showSnackBar(
    view: View,
    text: CharSequence,
    duration: Int = Snackbar.LENGTH_SHORT,
    @IdRes anchorViewId: Int = View.NO_ID,
    action: Snackbar.() -> Unit = {}
) = Snackbar.make(view, text, duration).apply {
    // FIXME:
    // setAnchorView(anchorViewId) memory leak,
    // [FabSnackBarAwareBehavior] for alternative
    // BaseTransientBottomBar#anchorViewLayoutListener
    // if (anchorViewId != View.NO_ID) setAnchorView(anchorViewId)
    Timber.d("ignored $anchorViewId")
    action(this)
}.show()
