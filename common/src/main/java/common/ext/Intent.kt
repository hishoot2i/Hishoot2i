@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.content.Intent
import android.net.Uri

// inline fun Uri.toActionSendImage(): Intent = Intent(Intent.ACTION_SEND)
//    .setDataAndTypeAndNormalize(this, "image/*")
//    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

inline fun Uri.toActionViewImage(): Intent = Intent(Intent.ACTION_VIEW)
    .setDataAndTypeAndNormalize(this, "image/*")
    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

@Suppress("DEPRECATION")
inline fun Uri.toActionMediaScanner(): Intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    .setData(this)
    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

inline fun actionMainWith(category: String): Intent = Intent(Intent.ACTION_MAIN)
    .addCategory(category)

inline fun actionGetContentWith(type: String): Intent = Intent(Intent.ACTION_GET_CONTENT)
    .setType(type)
    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

inline fun chooserGetContentWith(type: String, title: String): Intent =
    Intent.createChooser(actionGetContentWith(type), title)

@Suppress("DEPRECATION")
inline fun actionUninstallApk(packageName: String): Intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
    .setData(Uri.parse("package:$packageName"))
    .putExtra(Intent.EXTRA_RETURN_RESULT, true)
