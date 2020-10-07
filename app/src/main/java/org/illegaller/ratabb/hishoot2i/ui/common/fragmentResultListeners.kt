package org.illegaller.ratabb.hishoot2i.ui.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener

fun Fragment.setFragmentResultListeners(
    vararg requestKeys: String,
    listener: ((requestKey: String, bundle: Bundle) -> Unit)
) {
    for (requestKey in requestKeys) setFragmentResultListener(requestKey, listener)
}

fun Fragment.clearFragmentResultListeners(vararg requestKeys: String) {
    for (requestKey in requestKeys) clearFragmentResultListener(requestKey)
}