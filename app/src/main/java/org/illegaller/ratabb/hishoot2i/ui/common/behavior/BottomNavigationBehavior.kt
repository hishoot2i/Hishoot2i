package org.illegaller.ratabb.hishoot2i.ui.common.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Keep
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

@Keep
class BottomNavigationBehavior @JvmOverloads @Keep constructor(
    context: Context? = null,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<BottomNavigationView>(context, attrs) {
    private var isSnackBarAppear: Boolean = false
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: BottomNavigationView,
        dependency: View
    ): Boolean = dependency is Snackbar.SnackbarLayout

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: BottomNavigationView,
        dependency: View
    ): Boolean {
        when (dependency) {
            is Snackbar.SnackbarLayout -> {
                if (isSnackBarAppear) return true
                isSnackBarAppear = true
                child.let {
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
        parent: CoordinatorLayout,
        child: BottomNavigationView,
        dependency: View
    ) {
        when (dependency) {
            is Snackbar.SnackbarLayout -> isSnackBarAppear = false
            else -> super.onDependentViewRemoved(parent, child, dependency)
        }
    }
}
