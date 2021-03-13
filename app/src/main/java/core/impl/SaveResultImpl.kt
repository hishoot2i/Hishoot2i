package core.impl

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.provider.MediaStore.Images.ImageColumns
import android.provider.MediaStore.Images.Media
import androidx.core.content.contentValuesOf
import common.FileConstants
import common.ext.graphics.saveTo
import common.ext.graphics.sizes
import common.ext.toDateTimeFormat
import core.Save
import core.SaveResult
import entity.Sizes
import entity.ext
import entity.mimeType
import java.io.File
import javax.inject.Inject

class SaveResultImpl @Inject constructor(
    resolver: ContentResolver,
    fileConstants: FileConstants
) : SaveResult {
    private val resolverInsert: (Uri, ContentValues) -> Uri? = (resolver::insert)
    private val savedDir: () -> File = (fileConstants::savedDir)
    private val toUri: (File) -> Uri = (fileConstants::toUri)

    // TODO: Implement [Scoped storage] here and `somewhere` ?
    //  - https://developer.android.com/training/data-storage/use-cases
    //  - https://commonsware.com/blog/2019/12/21/scoped-storage-stories-storing-mediastore.html
    override suspend fun savingIt(
        bitmap: Bitmap,
        compressFormat: CompressFormat,
        saveQuality: Int
    ): Save {
        val nowMs = System.currentTimeMillis()
        val (ext, mimeType) = compressFormat.run { ext to mimeType }
        @Suppress("SpellCheckingInspection")
        val fileName = "HiShoot_${nowMs.toDateTimeFormat("yyyyMMdd_HHmmss")}.$ext"
        val file = File(savedDir(), fileName)
            .also { bitmap.saveTo(it, compressFormat, saveQuality) }
        val uri = resolverInsert(
            Media.EXTERNAL_CONTENT_URI,
            contentValues(nowMs, bitmap.sizes, file, fileName, mimeType)
        ) ?: toUri(file) //
        return Save(bitmap, uri, fileName)
    }

    private fun contentValues(
        nowMs: Long,
        bitmapSize: Sizes,
        file: File,
        fileName: String,
        mimeType: String
    ): ContentValues {
        val nowInSeconds = nowMs / 1000
        val (width, height) = bitmapSize
        return contentValuesOf(
            @Suppress("DEPRECATION") // DATA
            ImageColumns.DATA to file.absolutePath,
            ImageColumns.TITLE to fileName,
            ImageColumns.DISPLAY_NAME to fileName,
            ImageColumns.DATE_ADDED to nowInSeconds,
            ImageColumns.DATE_MODIFIED to nowInSeconds,
            ImageColumns.MIME_TYPE to mimeType,
            ImageColumns.WIDTH to width,
            ImageColumns.HEIGHT to height,
            ImageColumns.SIZE to file.length()
        ).also {
            if (SDK_INT >= 29) it.put(ImageColumns.DATE_TAKEN, nowMs)
        }
    }
}
