package org.illegaller.ratabb.hishoot2i.ui.common.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.Keep
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
/**
 * We use this, because [Snackbar.setAnchorView]  buggy
 **/
@Keep
open class FabSnackBarAwareBehavior @JvmOverloads @Keep constructor(
    context: Context? = null,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<FloatingActionButton>(context, attrs) {
    private var isSnackBarAppear: Boolean = false
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View
    ): Boolean = dependency is Snackbar.SnackbarLayout

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View
    ): Boolean {
        when (dependency) {
            is Snackbar.SnackbarLayout -> {
                if (isSnackBarAppear) return true
                isSnackBarAppear = true
                dependency.apply {
                    val translateY = (child.height - child.translationY).toInt()
                    (layoutParams as MarginLayoutParams).bottomMargin += translateY
                    requestLayout()
                }
                return true
            }
            else -> return false
        }
    }

    override fun onDependentViewRemoved(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View
    ) {
        when (dependency) {
            is Snackbar.SnackbarLayout -> isSnackBarAppear = false
            else -> super.onDependentViewRemoved(parent, child, dependency)
        }
    }
}
