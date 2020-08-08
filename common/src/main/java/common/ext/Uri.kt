@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.content.ContentResolver
import android.content.ContentResolver.SCHEME_CONTENT
import android.content.ContentResolver.SCHEME_FILE
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.KITKAT
import android.os.Environment.getExternalStorageDirectory
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.webkit.MimeTypeMap
import androidx.core.content.ContentResolverCompat
import androidx.documentfile.provider.DocumentFile
import timber.log.Timber
import java.io.File

inline fun Uri.toFile(context: Context): File? = when (scheme) {
    SCHEME_FILE -> path?.let { File(it) }
    SCHEME_CONTENT -> {
        val contentResolver: ContentResolver = context.contentResolver
        val path: String?
        if (SDK_INT >= KITKAT && DocumentFile.isDocumentUri(context, this)) {
            var documentId = DocumentsContract.getDocumentId(this)
            when {
                isDownloadDocument -> {
                    documentId = documentId.replace("raw:", "")
                }
                isExternalStorage -> {
                    @Suppress("DEPRECATION")
                    documentId = File(
                        getExternalStorageDirectory(),
                        documentId.replace("primary:", "")
                    ).absolutePath
                }
                isImageMediaDocument -> {
                    @Suppress("DEPRECATION") val column =
                        arrayOf(MediaStore.Images.ImageColumns.DATA)
                    val sel = "${MediaStore.Images.Media._ID}=?"
                    val selArg = arrayOf(documentId.split(":")[1])
                    documentId = EXTERNAL_CONTENT_URI.resolveFrom(
                        contentResolver,
                        column,
                        sel,
                        selArg,
                        column[0]
                    )
                }
            }
            path = documentId
        } else {
            path = when {
                isImageMedia -> {
                    @Suppress("DEPRECATION") val column = MediaStore.Images.ImageColumns.DATA
                    resolveFrom(
                        contentResolver,
                        projection = arrayOf(column),
                        columnName = column
                    )
                }
                isMedia -> resolveFrom(contentResolver, columnName = "_data")
                else -> {
                    null
                }
            }
        }
        path?.let { File(it) }
    }
    else -> null
}

@JvmOverloads
inline fun Uri.resolveFrom(
    contentResolver: ContentResolver,
    projection: Array<String>? = null,
    selection: String? = null,
    selectionArg: Array<String>? = null,
    columnName: String
): String? {
    var cursor: Cursor? = null
    try {
        cursor = ContentResolverCompat.query(
            contentResolver,
            this,
            projection,
            selection,
            selectionArg,
            null,
            null
        )
        cursor.moveToFirst()
        val index = cursor.getColumnIndex(columnName)
        return cursor.getString(index)
            .also { Timber.d("Uri<<<<:$it") }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        cursor?.close()
    }
}

inline val Uri.isDownloadDocument: Boolean
    get() = authority == "com.android.providers.downloads.documents"
inline val Uri.isExternalStorage: Boolean
    get() = authority == "com.android.externalstorage.documents"
inline val Uri.isImageMediaDocument: Boolean
    get() = if (SDK_INT >= KITKAT) {
        DocumentsContract.getDocumentId(this)
            .startsWith(prefix = "image", ignoreCase = true)
    } else false
inline val Uri.isMedia: Boolean
    get() = authority == MediaStore.AUTHORITY
inline val Uri.isImageMedia: Boolean
    get() = try {
        val ext = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        isMedia && mimeType != null && mimeType.startsWith(prefix = "image/", ignoreCase = true)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
