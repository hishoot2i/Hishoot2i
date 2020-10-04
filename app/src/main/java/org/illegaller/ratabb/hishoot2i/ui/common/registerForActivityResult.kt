package org.illegaller.ratabb.hishoot2i.ui.common

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
/** Shortcut for register [RequestPermission]
 * @see [android.app.Activity.requestPermissions]
 **/
inline fun ComponentActivity.registerRequestPermissionLazy(
    crossinline callback: (Boolean) -> Unit
) = lazy {
    registerForActivityResult(RequestPermission()) { callback(it) }
}

inline fun Fragment.registerGetContent(
    crossinline callback: (Uri) -> Unit
) = registerForActivityResult(GetContent()) { uri -> uri?.let { callback(it) } }

@RequiresApi(21)
inline fun Fragment.registerOpenDocumentTree(
    crossinline callback: (Uri) -> Unit
) = registerForActivityResult(OpenDocumentTree()) { uri -> uri?.let { callback(it) } }
