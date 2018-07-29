package org.illegaller.ratabb.hishoot2i.ui.common.behavior

import android.content.Context
import android.support.annotation.Keep
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View

@Keep
class BottomNavigationBehavior @JvmOverloads @Keep constructor(
    context: Context? = null,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<BottomNavigationView>(context, attrs) {
    private var isSnackBarAppear: Boolean = false
    override fun layoutDependsOn(
        parent: CoordinatorLayout?,
        child: BottomNavigationView?,
        dependency: View?
    ): Boolean = dependency is Snackbar.SnackbarLayout

    override fun onDependentViewChanged(
        parent: CoordinatorLayout?,
        child: BottomNavigationView?,
        dependency: View?
    ): Boolean {
        when (dependency) {
            is Snackbar.SnackbarLayout -> {
                if (isSnackBarAppear) return true
                isSnackBarAppear = true
                child?.let {
                    dependency.apply {
                        val translateY = (child.height - child.translationY).toInt()
                        setPadding(paddingLeft, paddingTop, paddingRight, translateY)
                        requestLayout()
                    }
                }
                return true
            }
            else -> return false
        }
    }

    override fun onDependentViewRemoved(
        parent: CoordinatorLayout?,
        child: BottomNavigationView?,
        dependency: View?
    ) {
        when (dependency) {
            is Snackbar.SnackbarLayout -> isSnackBarAppear = false
            else -> super.onDependentViewRemoved(parent, child, dependency)
        }
    }
}
