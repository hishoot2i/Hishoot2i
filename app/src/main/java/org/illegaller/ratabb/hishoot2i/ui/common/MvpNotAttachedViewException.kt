package org.illegaller.ratabb.hishoot2i.ui.common

class MvpNotAttachedViewException : RuntimeException(MESSAGE) {
    companion object {
        private const val MESSAGE = "Do call attachView(View) before requesting data."
    }
}