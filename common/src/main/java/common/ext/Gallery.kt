@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import timber.log.Timber
import java.io.File

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
inline fun Context.addToGallery(uriToAdd: Uri) {
    Timber.d("Begin MediaScanner...\n ${uriToAdd.path}")
    try {
        val file = Environment.getExternalStorageDirectory().let {
            File(it, uriToAdd.path?.replace("/storage", ""))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(
                applicationContext,
                arrayOf(file.absolutePath),
                arrayOf("image/png")
            ) { path, uri -> Timber.d("path:$path, uri:$uri") }
        } else sendBroadcast(Uri.fromFile(file).toActionMediaScanner())
    } catch (e: Exception) {
        Timber.e(e)
    }
}
