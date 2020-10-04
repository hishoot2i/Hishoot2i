package org.illegaller.ratabb.hishoot2i.ui.common

import android.view.View

private const val EDGE_TO_EDGE_FLAGS = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

fun setSystemUiFlagEdgeToEdge(view: View, enabled: Boolean) {
    view.systemUiVisibility = view.systemUiVisibility and
            EDGE_TO_EDGE_FLAGS.inv() or if (enabled) EDGE_TO_EDGE_FLAGS else 0
}
